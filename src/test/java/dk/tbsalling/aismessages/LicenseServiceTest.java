package dk.tbsalling.aismessages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.verhas.licensor.License;
import dk.tbsalling.aismessages.commercial.LicenseService;
import dk.tbsalling.aismessages.commercial.LicenseServiceImpl;
import org.bouncycastle.openpgp.PGPException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class LicenseServiceTest {

    @BeforeClass
    public static void setUp() {
    }

    enum LicenseStatus { LICENSE_VALID, LICENSE_EXPIRED, PRODUCT_INVALID, MAJOR_RELEASE_INVALID, NOT_VERIFIED, LICENSE_NOT_FOUND, LICENSE_DECODE_FAILED };

    @Test
    public void canVerifyValidLicense() {
        LicenseService licenseService = new LicenseServiceImpl("aismessages-valid.lic", "AISmessages", 1);
        assertEquals(LicenseService.LicenseStatus.LICENSE_VALID, licenseService.getLicenseStatus());
        assertTrue(licenseService.isLicenseValid());
    }

    @Test
    public void canNotVerifyExpiredLicense() {
        LicenseService licenseService = new LicenseServiceImpl("aismessages-expired.lic", "AISmessages", 1);
        assertEquals(LicenseService.LicenseStatus.LICENSE_EXPIRED, licenseService.getLicenseStatus());
        assertFalse(licenseService.isLicenseValid());
    }

    @Test
    public void canNotVerifyWrongProductLicense() {
        LicenseService licenseService = new LicenseServiceImpl("aismessages-wrongproduct.lic", "AISmessages", 1);
        assertEquals(LicenseService.LicenseStatus.PRODUCT_INVALID, licenseService.getLicenseStatus());
        assertFalse(licenseService.isLicenseValid());
    }

    @Test
    public void canNotVerifyWrongMajorReleaseLicense() {
        LicenseService licenseService = new LicenseServiceImpl("aismessages-wrongmajorrelease.lic", "AISmessages", 1);
        assertEquals(LicenseService.LicenseStatus.MAJOR_RELEASE_INVALID, licenseService.getLicenseStatus());
        assertFalse(licenseService.isLicenseValid());
    }

    @Test
    public void canNotVerifyWrongMajorReleaseLicense2() {
        LicenseService licenseService = new LicenseServiceImpl("aismessages-valid.lic", "AISmessages", 2);
        assertEquals(LicenseService.LicenseStatus.MAJOR_RELEASE_INVALID, licenseService.getLicenseStatus());
        assertFalse(licenseService.isLicenseValid());
    }

    @Test
    public void canNotVerifyWrongSignatureKey() {
        LicenseService licenseService = new LicenseServiceImpl("aismessages-wrongkey.lic", "AISmessages", 1);
        assertEquals(LicenseService.LicenseStatus.NOT_VERIFIED, licenseService.getLicenseStatus());
        assertFalse(licenseService.isLicenseValid());
    }

}
