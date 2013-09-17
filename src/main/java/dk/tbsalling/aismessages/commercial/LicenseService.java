package dk.tbsalling.aismessages.commercial;

public interface LicenseService {
    enum LicenseStatus { LICENSE_VALID, LICENSE_EXPIRED, PRODUCT_INVALID, MAJOR_RELEASE_INVALID, NOT_VERIFIED, LICENSE_NOT_FOUND, LICENSE_DECODE_FAILED };

    Boolean isLicenseValid();
    LicenseStatus getLicenseStatus();
    String getLicenseStatusAsString();
    String getLicenseText();
}
