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
package org.openmrs.logic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that will be used to write log entries when forking a new process on the operating system. 
 * 
 * @see org.openmrs.logic.util.LogicUtil#executeCommand(java.lang.String[], java.io.File)
 */
class StreamHandler implements Runnable {
	
    private static final Logger log = LoggerFactory.getLogger(StreamHandler.class);
	
	private final InputStream stream;
	
	private final String source;
	
	public StreamHandler(InputStream stream, String source) {
		this.stream = stream;
		this.source = source;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			while ((line = reader.readLine()) != null)
				log.info("{}: {}", this.source, line);
			reader.close();
		}
		catch (IOException e) {
			log.error("Handling stream from runtime exec failed ...", e);
		}
	}
}
