package moe.yo3explorer.mfcProxyTest;

import io.quarkus.test.junit.QuarkusTest;
import moe.yo3explorer.mfcProxy.boundary.FrontPage;
import moe.yo3explorer.mfcProxy.boundary.PartnerRedirect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

@QuarkusTest
public class MfcProxyTest {

    @Inject
    FrontPage frontPage;

    @Inject
    PartnerRedirect partnerRedirect;

    @Test
    public void doFrontPageTest()
    {
        Response frontPage = this.frontPage.getFrontPage();
        Assertions.assertEquals(frontPage.getStatus(),200);

        Response partner = this.partnerRedirect.getPartner(66);
        Assertions.assertEquals(frontPage.getStatus(),200);
    }
}
