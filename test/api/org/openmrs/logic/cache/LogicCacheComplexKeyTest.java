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
package org.openmrs.logic.cache;

import net.sf.ehcache.Element;
import org.junit.Test;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.impl.LogicServiceImpl;
import org.openmrs.logic.result.Result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class LogicCacheComplexKeyTest {
    @Test
    public void testEquals() throws Exception {
        LogicCacheComplexKey logicCacheComplexKey1 = new LogicCacheComplexKey();
        LogicCacheComplexKey logicCacheComplexKey2 = new LogicCacheComplexKey();

        assertEquals("Comparing empty keys.", logicCacheComplexKey1, logicCacheComplexKey2);


        LogicService logicService = new LogicServiceImpl();
        LogicCriteria logicCriteria1 = logicService.parse("\"AGE\"");
        LogicCriteria logicCriteria2 = logicService.parse("\"AGE\"");

//        assertEquals("Comparing criteria.", logicCriteria1, logicCriteria2);

        logicCacheComplexKey1 = new LogicCacheComplexKey(null, null, logicCriteria1, null);
        logicCacheComplexKey2 = new LogicCacheComplexKey(null, null, logicCriteria2, null);

        assertEquals("Comparing keys with criteria.", logicCacheComplexKey1, logicCacheComplexKey2);

        Map<Integer, Result> resultMap = new Hashtable<Integer, Result>();
        Result result = new Result(true);
        resultMap.put(1, result);

        Element el = new Element(logicCacheComplexKey1, resultMap);
        System.out.println("size=" + el.getSerializedSize());

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(el);
            oos.close();
            assertTrue(out.toByteArray().length > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element elSimple = new Element(1, new Double("250.0004"));
        System.out.println("size=" + elSimple.getSerializedSize());

    }
}
