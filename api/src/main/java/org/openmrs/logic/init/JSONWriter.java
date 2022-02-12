package org.openmrs.logic.init;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONWriter {

    private static final int TWELVE = 12;
    private static final int FOUR = 4;
    private static final Logger LOG = LoggerFactory.getLogger(JSONWriter.class);
    private static char[] hex = "0123456789ABCDEF".toCharArray();

    private StringBuffer buf = new StringBuffer();
    private Deque<Object> calls = new ArrayDeque<>();
    boolean emitClassName = true;
    
    public JSONWriter(boolean emitClassName) {
        this.emitClassName = emitClassName;
    }
    
    public JSONWriter() {
        this(true);
    }

    public String write(Object object) {
        this.buf.setLength(0);
        value(object);
        return this.buf.toString();
    }

    public String write(long n) {
        return String.valueOf(n);
    }

    public String write(double d) {
        return String.valueOf(d);
    }

    public String write(char c) {
        return "\"" + c + "\"";
    }
    
    public String write(boolean b) {
        return String.valueOf(b);
    }

    private void value(Object object) {
        if (object == null) {
            add("null");
        } else if (object instanceof Class) {
            string(object);
        } else if (object instanceof Boolean) {
            bool(((Boolean) object).booleanValue());
        } else if (object instanceof Number) {
            add(object);
        } else if (object instanceof String) {
            string(object);
        } else if (object instanceof Character) {
            string(object);
        } else if (object instanceof Map) {
            map((Map<?, ?>) object);
        } else if (object.getClass().isArray()) {
            array(object);
        } else if (object instanceof Iterable) {
            array(((Iterable<?>) object).iterator());
        } else {
            bean(object);
        }
    }

    private void bean(Object object) {
        if (this.calls.contains(object)) {
            add(null);
            return;
        }
        this.calls.push(object);

        add("{");
        BeanInfo info;
        boolean addedSomething = false;
        try {
            info = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (int i = 0; i < props.length; ++i) {
                PropertyDescriptor prop = props[i];
                String name = prop.getName();
                Method accessor = prop.getReadMethod();
                if ((this.emitClassName || !"class".equals(name)) && accessor != null) {
                    Object value = accessor.invoke(object, (Object[])null);
                    if (addedSomething) {
                        add(',');
                    }
                    add(name, value);
                    addedSomething = true;
                }
            }
            Field[] ff = object.getClass().getFields();
            for (int i = 0; i < ff.length; ++i) {
                Field field = ff[i];
                if (addedSomething) {
                    add(',');
                }
                add(field.getName(), field.get(object));
                addedSomething = true;
            }
        } catch (Exception e) { 
            LOG.error(e.getMessage());
        }
        add("}");
        this.calls.pop();
    }

    private void add(String name, Object value) {
        add('"');
        add(name);
        add("\":");
        value(value);
    }

    @SuppressWarnings("rawtypes")
    private void map(Map<?, ?> map) {
        add("{");
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            value(e.getKey());
            add(":");
            value(e.getValue());
            if (it.hasNext()) {
                add(',');
            }
        }
        add("}");
    }
    
    private void array(Iterator<?> it) {
        add("[");
        while (it.hasNext()) {
            value(it.next());
            if (it.hasNext()) {
                add(",");
            }
        }
        add("]");
    }

    private void array(Object object) {
        add("[");
        int length = Array.getLength(object);
        for (int i = 0; i < length; ++i) {
            value(Array.get(object, i));
            if (i < length - 1) {
                add(',');
            }
        }
        add("]");
    }

    private void bool(boolean b) {
        add(b ? "true" : "false");
    }

    private void string(Object obj) {
        add('"');
        CharacterIterator it = new StringCharacterIterator(obj.toString());
        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (c == '"') {
                add("\\\"");
            } else if (c == '\\') {
                add("\\\\");
            } else if (c == '/') {
                add("\\/");
            } else if (c == '\b') {
                add("\\b");
            } else if (c == '\f') {
                add("\\f");
            } else if (c == '\n') {
                add("\\n");
            } else if (c == '\r') {
                add("\\r");
            } else if (c == '\t') {
                add("\\t");
            } else if (Character.isISOControl(c)) {
                unicode(c);
            } else {
                add(c);
            }
        }
        add('"');
    }

    private void add(Object obj) {
        this.buf.append(obj);
    }

    private void add(char c) {
        this.buf.append(c);
    }

    private void unicode(char c) {
        add("\\u");
        int n = c;
        for (int i = 0; i < FOUR; ++i) {
            int digit = (n & 0xf000) >> TWELVE;
            add(hex[digit]);
            n <<= FOUR;
        }
    }

}
