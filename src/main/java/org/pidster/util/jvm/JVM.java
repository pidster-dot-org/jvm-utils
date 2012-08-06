/*
 *  Copyright 2012 The original authors
 *  
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.pidster.util.jvm;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;


/**
 * @author <a href="http://pidster.com/">pidster</a>
 *
 */
public class JVM {
    
    public static void loadAgent(String pid, File jar) throws GenericAgentException {
        loadAgent(pid, jar, null);
    }

    public static void loadAgent(String pid, File jar, String options) throws GenericAgentException {
        loadAgent(pid, jar.getAbsolutePath(), options);
    }

    public static void loadAgent(String pid, String jarPath) throws GenericAgentException {
        loadAgent(pid, jarPath, (String) null);
    }

    public static void loadAgent(String pid, String jarPath, Map<String, String> options) throws GenericAgentException {
        StringBuilder s = new StringBuilder();

        Set<Entry<String, String>> entrySet = options.entrySet();

        boolean first = true;
        for (Entry<String, String> e : entrySet) {
            if (first) {
                first = false;
            }
            else {
                s.append(",");
            }
            s.append(e.getKey());
            s.append("=");
            s.append(e.getValue());
        }

        loadAgent(pid, jarPath, s.toString());
    }

    public static void loadAgent(String pid, String jarPath, String options) throws GenericAgentException {

        VirtualMachine machine = null;

        try {
            try {
                machine = VirtualMachine.attach(pid);

                if (options == null || "".equals(options)) {
                    machine.loadAgent(jarPath);
                }
                else {
                    machine.loadAgent(jarPath, options);
                }

            } catch (AttachNotSupportedException e) {
                throw new GenericAgentException(e);
            } catch (IOException e) {
                throw new GenericAgentException(e);
            } catch (AgentLoadException e) {
                throw new GenericAgentException(e);
            } catch (AgentInitializationException e) {
                throw new GenericAgentException(e);
            }
        }
        finally {
            if (machine == null) {
                return;
            }

            try {
                machine.detach();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
