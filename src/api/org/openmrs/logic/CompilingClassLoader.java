/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.logic.util.LogicUtil;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

/**
 * Class loader that will automatically compile source as necessary when looking for class files.
 * This class only deal with the rule java and class file. Separate handler must be registered to create the java file.
 * 
 * Implementation based on http://www.ibm.com/developerworks/edu/j-dw-javaclass-i.html and code from Tammy Dugan and Vibha Anand
 * in the dss module.
 * 
 * @see org.openmrs.logic.rule.definition.service.impl.LanguageHandler
 */
public class CompilingClassLoader extends ClassLoader {
	
	protected Log log = LogFactory.getLog(CompilingClassLoader.class);
	
	// This class only deal with the rule java and class file. Processing from arden to java will be performed
	// using ArdenService from the core OpenMRS
	// Given a filename, read the entirety of that file from disk
	// and return it as a byte array.
	private byte[] getBytes(String filename) throws IOException {
		// Find out the length of the file
		File file = new File(filename);
		long len = file.length();
		
		// Create an array that's just the right size for the file's
		// contents
		byte raw[] = new byte[(int) len];
		
		// Open the file
		FileInputStream fin = new FileInputStream(file);
		
		// Read all of it into the array; if we don't get all,
		// then it's an error.
		int r = fin.read(raw);
		if (r != len)
			throw new IOException("Can't read all bytes from java file, " + r + " != " + len);
		
		// Don't forget to close the file!
		fin.close();
		
		// And finally return the file contents as an array
		return raw;
	}
	
	private List<File> getCompilerClasspath() {
		List<File> files = new ArrayList<File>();
		Collection<ModuleClassLoader> moduleClassLoaders = ModuleFactory.getModuleClassLoaders();
		
		//check module dependencies
		for (ModuleClassLoader moduleClassLoader : moduleClassLoaders) {
			URL[] urls = moduleClassLoader.getURLs();
			for (URL url : urls)
				files.add(new File(url.getFile()));
		}
		
		// check current class loader and all its parents
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		while (classLoader != null) {
			if (classLoader instanceof URLClassLoader) {
				URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
				URL[] urls = urlClassLoader.getURLs();
				for (URL url : urls)
					files.add(new File(url.getFile()));
			}
			classLoader = classLoader.getParent();
		}
		
		return files;
		
	}
	
	// Spawn a process to compile the java source code file
	// specified in the 'javaFile' parameter.  Return a true if
	// the compilation worked, false otherwise.
	private boolean compile(String javaFile) throws IOException {
		boolean compiled = false;
		String ruleClassDir = Context.getAdministrationService().getGlobalProperty(LogicConstants.RULE_DEFAULT_CLASS_FOLDER);
		String ruleJavaDir = Context.getAdministrationService().getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		log.info("JavaCompiler is null: " + compiler == null);
		if (compiler != null) {
			// java compiler only available on JDK. This part of "IF" will not get executed when we run JUnit test
			File outputFolder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleClassDir);
			String[] options = {};
			DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, Context.getLocale(), Charset.defaultCharset());
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputFolder));
			// create compiling classpath
			String stringProperties = System.getProperty("java.class.path");
			if (log.isDebugEnabled())
				log.debug("Compiler classpath: " + stringProperties);
			String[] properties = StringUtils.split(stringProperties, File.pathSeparator);
			List<File> classpathFiles = new ArrayList<File>();
			for (String property : properties) {
				File f = new File(property);
				if (f.exists())
					classpathFiles.add(f);
			}
			classpathFiles.addAll(getCompilerClasspath());
			fileManager.setLocation(StandardLocation.CLASS_PATH, classpathFiles);
			// create the compilation task
			CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnosticCollector, Arrays.asList(options), null, fileManager.getJavaFileObjects(javaFile));
			compiled = compilationTask.call();
			for (Diagnostic<?> diagnostic : diagnosticCollector.getDiagnostics()) {
				log.error("Error line: " + diagnostic.getLineNumber());
				log.error("Error message: " + diagnostic.getMessage(Context.getLocale()));
			}
			fileManager.close();
		} else {
			File outputFolder = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleClassDir);
			String[] commands = { "javac", "-classpath", System.getProperty("java.class.path"), "-d", outputFolder.getAbsolutePath(), javaFile };
			// Start up the compiler
			File workingDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir);
			compiled = LogicUtil.executeCommand(commands, workingDirectory);
		}
		
		return compiled;
	}
	
	// The heart of the ClassLoader -- automatically compile
	// source as necessary when looking for class files
	/**
	 * @see java.lang.ClassLoader#loadClass(String, boolean)
	 * @should compile and load java file at runtime
	 */
	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		
		// Our goal is to get a Class object
		Class<?> clas = null;
		
		// First, see if we've already dealt with this one
		clas = findLoadedClass(name);
		
		// Create a pathname from the class name
		// E.g. java.lang.Object => java/lang/Object
		String fileStub = name.replace('.', File.separatorChar);
		
		// Build objects pointing to the source code (.java) and object
		// code (.class)
		String javaFilename = fileStub + ".java";
		String classFilename = fileStub + ".class";
		
		String ruleClassDir = Context.getAdministrationService().getGlobalProperty(LogicConstants.RULE_DEFAULT_CLASS_FOLDER);
		String ruleJavaDir = Context.getAdministrationService().getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		
		File javaFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir), javaFilename);
		File classFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleClassDir), classFilename);
		
		// First, see if we want to try compiling.  We do if (a) there
		// is source code, and either (b0) there is no object code,
		// or (b1) there is object code, but it's older than the source
		if (javaFile.exists() && (!classFile.exists() || javaFile.lastModified() > classFile.lastModified())) {
			
			try {
				// Try to compile it.  If this doesn't work, then
				// we must declare failure.  (It's not good enough to use
		
				// and already-existing, but out-of-date, class file)
				if (!compile(javaFile.getAbsolutePath()) || !classFile.exists())
					throw new ClassNotFoundException("Compilation process failed for " + javaFilename);
			}
			catch (IOException ie) {
				
				// Another place where we might come to if we fail
				// to compile
				throw new ClassNotFoundException(ie.toString(), ie);
			}
		}
		
		// Let's try to load up the raw bytes, assuming they were
		// properly compiled, or didn't need to be compiled
		try {
			
			// read the bytes
			byte raw[] = getBytes(classFile.getAbsolutePath());
			
			// try to turn them into a class
			clas = defineClass(name, raw, 0, raw.length);
		}
		catch (IOException ie) {
			// This is not a failure!  If we reach here, it might
			// mean that we are dealing with a class in a library,
			// such as java.lang.Object
		}
		
		// Maybe it is in the openmrs loader
		if (clas == null)
			clas = OpenmrsClassLoader.getInstance().loadClass(name);
		
		// Maybe the class is in a library -- try loading
		// the normal way
		if (clas == null)
			clas = findSystemClass(name);
		
		// Resolve the class, if any, but only if the "resolve"
		// flag is set to true
		if (resolve && clas != null)
			resolveClass(clas);
		
		// If we still don't have a class, it's an error
		if (clas == null)
			throw new ClassNotFoundException(name);
		
		// Otherwise, return the class
		return clas;
	}
}
