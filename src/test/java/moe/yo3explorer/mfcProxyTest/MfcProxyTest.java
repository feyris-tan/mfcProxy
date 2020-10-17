package moe.yo3explorer.mfcProxyTest;

import io.quarkus.test.junit.QuarkusTest;
import moe.yo3explorer.mfcProxy.boundary.FrontPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

@QuarkusTest
public class MfcProxyTest {

    @Inject
    FrontPage frontPage;

    @Test
    public void doFrontPageTest()
    {
        Response frontPage = this.frontPage.getFrontPage();
        Assertions.assertEquals(frontPage.getStatus(),200);
    }
}
