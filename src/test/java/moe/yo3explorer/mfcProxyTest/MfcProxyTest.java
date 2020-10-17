package moe.yo3explorer.mfcProxyTest;

import io.quarkus.test.junit.QuarkusTest;
import moe.yo3explorer.mfcProxy.boundary.Ad;
import moe.yo3explorer.mfcProxy.boundary.FrontPage;
import moe.yo3explorer.mfcProxy.boundary.Partner;
import moe.yo3explorer.mfcProxy.boundary.Picture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

@QuarkusTest
public class MfcProxyTest {

    @Inject
    FrontPage frontPage;

    @Inject
    Partner partnerRedirect;

    @Inject
    Picture picture;

    @Inject
    Ad ad;

    @Test
    public void doFrontPageTest()
    {
        Response frontPage = this.frontPage.getFrontPage();
        Assertions.assertEquals(frontPage.getStatus(),200);

        Response partner = this.partnerRedirect.getPartner(66);
        Assertions.assertEquals(partner.getStatus(),200);

        Response picture = this.picture.getPicture(2512943);
        Assertions.assertEquals(picture.getStatus(),200);

        Response picture1 = this.picture.getPicture(2523839);
        Assertions.assertEquals(picture1.getStatus(),200);

        Response picture2 = this.picture.getPicture(2346160);
        Assertions.assertEquals(picture2.getStatus(),200);

        Response ad = this.ad.getAd(218846);
        Assertions.assertEquals(ad.getStatus(),200);
    }
}
