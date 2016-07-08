/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 */

package com.commonsware.cwac.netsecurity.okhttp3.test.pub;

import android.support.test.InstrumentationRegistry;
import com.commonsware.cwac.netsecurity.CertificateNotMemorizedException;
import com.commonsware.cwac.netsecurity.MemorizingTrustManager;
import com.commonsware.cwac.netsecurity.TrustManagerBuilder;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class MemoSemiPersistentTest extends SimpleHTTPSTest {
  private MemorizingTrustManager memo;
  private boolean onNotMemorizedCalled=false;

  @Before
  public void initMemo()
    throws CertificateException, NoSuchAlgorithmException,
    KeyStoreException, IOException {
    MemorizingTrustManager.Options opts=new MemorizingTrustManager.Options(
      InstrumentationRegistry.getContext(), "memo", "sekrit");

    memo=new MemorizingTrustManager(opts);
  }

  @After
  public void cleanupMemo()
    throws CertificateException, NoSuchAlgorithmException,
    KeyStoreException, IOException {
    memo.clear(true);
  }

  @Override
  public void testRequest() throws Exception {
    super.testRequest();

    Assert.assertTrue("onNotMemorized() called", onNotMemorizedCalled);
    memo.clear(true);

    /*
      This is to confirm that memo.clear(true) worked, by re-running
      the test. If clear() fails, onNotMemorized() should not be called,
      since we will already have memorized the cert.
     */

    onNotMemorizedCalled=false;
    super.testRequest();
    Assert.assertTrue("onNotMemorized() called", onNotMemorizedCalled);

    /*
      So now we have the certificate memorized. Create a fresh
      MemorizingTrustManager instance, which should be reading from the
      file. In this case, we expect onNotMemorized() to *not* be called,
      since the cert is already memorized.
     */
    initMemo();
    onNotMemorizedCalled=false;
    super.testRequest();
    Assert.assertFalse("onNotMemorized() called", onNotMemorizedCalled);
  }

  @Override
  protected TrustManagerBuilder getBuilder() throws Exception {
    return(new TrustManagerBuilder().useDefault().and().add(memo));
  }

  @Override
  protected void onNotMemorized(CertificateNotMemorizedException e)
    throws Exception {
    if (!onNotMemorizedCalled) {
      onNotMemorizedCalled=true;
      memo.memorizeCert(e.getCertificateChain());
    }
    else {
      throw e;
    }
  }
}