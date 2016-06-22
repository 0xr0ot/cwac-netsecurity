/*
 * Copyright (C) 2015 The Android Open Source Project
 * Portions Copyright (C) 2016 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.cwac.netseccfg.os;

import android.os.Environment;
import java.io.File;

public class EnvironmentEx {
  public static File getUserConfigDirectory(int userId) {
    return new File(new File(new File(
      Environment.getDataDirectory(), "misc"), "user"),
      Integer.toString(userId));
  }
}