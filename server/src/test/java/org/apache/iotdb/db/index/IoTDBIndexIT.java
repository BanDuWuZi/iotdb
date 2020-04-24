/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.iotdb.db.index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.iotdb.db.utils.EnvironmentUtils;
import org.apache.iotdb.jdbc.Config;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IoTDBIndexIT {

  @Before
  public void setUp() throws Exception {
    EnvironmentUtils.envSetUp();
  }

  @After
  public void tearDown() throws Exception {
    EnvironmentUtils.cleanEnv();
  }


  @Test
  public void testParseIndexStatement() throws SQLException, ClassNotFoundException {
    Class.forName(Config.JDBC_DRIVER_NAME);
    try (Connection connection = DriverManager
        .getConnection(Config.IOTDB_URL_PREFIX + "127.0.0.1:6667/",
            "root", "root");
        Statement statement = connection.createStatement()) {
      statement.execute("SET STORAGE GROUP TO root.idx1");
      statement.execute("CREATE TIMESERIES root.idx1.d0.s0 WITH DATATYPE=INT32,ENCODING=PLAIN");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (1, 1)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (2, 2)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (3, 3)");
      statement.execute("flush");


      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (21, 21)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (22, 22)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (23, 23)");
      statement.execute ("flush");

      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (31, 31)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (32, 32)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (33, 33)");

      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (11, 11)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (12, 12)");
      statement.execute("INSERT INTO root.idx1.d0(timestamp, s0) VALUES (13, 13)");
      statement.execute("flush");

      statement.execute("DROP INDEX PAA ON root.idx1.d0.s0");
//      statement.execute("select index whole_st_time(s1), dist(s2) from root.vehicle.d1 where \"\n"
//          + "        + \"time <= 51 or !(time != 100 and time < 460) WITH INDEX=PAA, threshold=5, distance=DTW");

      System.out.println("finished!");

    }
  }


}
