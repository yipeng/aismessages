package dk.tbsalling.aismessages.commercial;

import com.verhas.licensor.License;
import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class LicenseServiceImpl implements LicenseService {
    private final byte[] digest;
    private final String licenseResourceName;
    private final License license;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String licenseText;

    private final String productName;
    private final Integer majorReleaseNumber;

    private LicenseStatus licenseStatus;

    public LicenseServiceImpl(String licenseResourceName, String productName, Integer majorReleaseNumber) {
        this.licenseResourceName = licenseResourceName;
        this.productName = productName;

        this.majorReleaseNumber = majorReleaseNumber;

        // ---KEY RING DIGEST START
        this.digest = new byte[] {
                (byte)0xCD,
                (byte)0x66, (byte)0xA1, (byte)0x21, (byte)0xD4, (byte)0x06, (byte)0x70, (byte)0x59, (byte)0xA3,
                (byte)0x52, (byte)0xD9, (byte)0xEA, (byte)0x11, (byte)0xB6, (byte)0x60, (byte)0xEA, (byte)0xCE,
                (byte)0xC3, (byte)0x86, (byte)0x29, (byte)0x6E, (byte)0x83, (byte)0x16, (byte)0xA0, (byte)0x5B,
                (byte)0x84, (byte)0x96, (byte)0x36, (byte)0xBD, (byte)0x56, (byte)0x5C, (byte)0xBA, (byte)0x7B,
                (byte)0x7C, (byte)0xBD, (byte)0x1C, (byte)0x52, (byte)0x0C, (byte)0x18, (byte)0xA0, (byte)0xCF,
                (byte)0x73, (byte)0x46, (byte)0x99, (byte)0x6F, (byte)0xEF, (byte)0x93, (byte)0xBA, (byte)0x29,
                (byte)0xB1, (byte)0x5B, (byte)0x2C, (byte)0xAD, (byte)0x10, (byte)0x30, (byte)0x9F, (byte)0x9D,
                (byte)0x2E, (byte)0xFB, (byte)0x1A, (byte)0x25, (byte)0xCE, (byte)0xE8, (byte)0xAD,
        };
        //---KEY RING DIGEST END

        this.license = new License();

        try {
            license.loadKeyRingFromResource("public.key", digest);
        } catch (IOException e) {
            this.licenseStatus = LicenseStatus.LICENSE_DECODE_FAILED;
        }

        try {
            license.setLicenseEncodedFromResource(this.licenseResourceName);
        } catch (PGPException e) {
            this.licenseStatus = LicenseStatus.LICENSE_DECODE_FAILED;
        } catch (IOException e) {
            this.licenseStatus = LicenseStatus.LICENSE_NOT_FOUND;
        } catch (NullPointerException e) {
            this.licenseStatus = LicenseStatus.LICENSE_NOT_FOUND;
        }

        this.validateLicense();

        this.licenseText = buildLicenseText();

        System.out.println(this.licenseText);
    }

    @Override
    public Boolean isLicenseValid() {
        return this.validateLicense() == LicenseStatus.LICENSE_VALID;
    }

    @Override
    public String getLicenseText() {
        return this.licenseText;
    }

    @Override
    public LicenseStatus getLicenseStatus() {
        return licenseStatus;
    }

    @Override
    public String getLicenseStatusAsString() {
        String licenceStatusAsText = null;

        switch (this.licenseStatus) {
            case LICENSE_VALID:
                licenceStatusAsText =  "License valid.";
                break;
            case LICENSE_EXPIRED:
                licenceStatusAsText =  "License expired on " + this.dateFormat.format(getValidUntil()) + ".";
                break;
            case PRODUCT_INVALID:
                licenceStatusAsText =  "License not valid for product " + this.productName + ".";
                break;
            case LICENSE_NOT_FOUND:
                licenceStatusAsText =  "Missing " + this.licenseResourceName + " resource.";
                break;
            case LICENSE_DECODE_FAILED:
                licenceStatusAsText =  "Cannot decode license resource.";  // "Missing public.key resource.
                break;
            case MAJOR_RELEASE_INVALID:
                licenceStatusAsText =  "License not valid for major release no. " + this.majorReleaseNumber + ".";
                break;
            case NOT_VERIFIED:
                licenceStatusAsText =  "License decode failed or license resource not found.";
                break;
            default:
                licenceStatusAsText =  "?";
        }
        return licenceStatusAsText;
    }

    private String buildLicenseText() {
        StringBuffer licenseTextBuffer = new StringBuffer();

        licenseTextBuffer.append("\n");
        licenseTextBuffer.append(this.productName);
        licenseTextBuffer.append(" is copyright (c) 2011-13 by S-Consult ApS, Denmark, CVR DK31327490. http://tbsalling.dk.\n");

        if (this.licenseStatus == LicenseStatus.LICENSE_VALID) {
            licenseTextBuffer.append("This version is commercially licensed to ");
            licenseTextBuffer.append(getLicenseHolderCompany());
            licenseTextBuffer.append(", ");
            licenseTextBuffer.append(getLicenseHolderCity());
            licenseTextBuffer.append(", ");
            licenseTextBuffer.append(getLicenseHolderCountry());
            licenseTextBuffer.append(". License terms apply.\n");
            licenseTextBuffer.append("License id: ");
            licenseTextBuffer.append(getLicenseId());
            licenseTextBuffer.append(". License type: ");
            licenseTextBuffer.append(getLicenseType());
            licenseTextBuffer.append(". Issued: ");
            licenseTextBuffer.append(this.dateFormat.format(getIssued()));
            licenseTextBuffer.append(". Expires: ");
            licenseTextBuffer.append(getValidUntil() == null ? "never":getValidUntil());
            licenseTextBuffer.append(".\n");
        } else {
            licenseTextBuffer.append("\n");
            licenseTextBuffer.append("This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. To view a copy of\n");
            licenseTextBuffer.append("this commercial, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send a letter to Creative Commons, 171 Second Street,\n");
            licenseTextBuffer.append("Suite 300, San Francisco, California, 94105, USA.\n");
            licenseTextBuffer.append("\n");
            licenseTextBuffer.append("NOT FOR COMMERCIAL USE!\n");
            licenseTextBuffer.append("\n");
            licenseTextBuffer.append("Contact tbsalling@tbsalling.dk to obtain commercially licensed software.\n");
        }

        licenseTextBuffer.append("\n");

        return licenseTextBuffer.toString();
    }

    private String getLicenseId() {
        return license.getFeature("license-id");
    }

    private String getLicenseHolderCompany() {
        return license.getFeature("license-holder-company");
    }

    private String getLicenseHolderCity() {
        return license.getFeature("license-holder-city");
    }

    private String getLicenseHolderCountry() {
        return license.getFeature("license-holder-country");
    }

    private String getLicenseHolderContactPerson() {
        return license.getFeature("license-holder-contact-person");
    }

    private String getLicenseType() {
        return license.getFeature("license-type");
    }

    private String getProduct() {
        return license.getFeature("product");
    }

    private Integer getProductMajorReleaseVersion() {
        return Integer.valueOf(license.getFeature("product-major-release-version"));
    }

    private Date getValidUntil() {
        String dateAsString = license.getFeature("valid-date");
        if (dateAsString != null && !dateAsString.trim().isEmpty()) {
            try {
                return this.dateFormat.parse(dateAsString);
            } catch (ParseException e) {
                return null;
            }
        } else
            return null;
    }

    private Date getIssued() {
        String dateAsString = license.getFeature("issue-date");
        try {
            return this.dateFormat.parse(dateAsString);
        } catch (ParseException e) {
            return null;
        }
    }

    private LicenseStatus validateLicense() {
        if (! checkLicenseIsVerified() ) {
            this.licenseStatus = LicenseStatus.NOT_VERIFIED;
        } else if (! checkDateValid(new Date(), getValidUntil())){
            this.licenseStatus = LicenseStatus.LICENSE_EXPIRED;
        } else if (! checkProductValid(this.productName, getProduct())) {
            this.licenseStatus = LicenseStatus.PRODUCT_INVALID;
        } else if (! checkMajorVersionValid(this.majorReleaseNumber, getProductMajorReleaseVersion())) {
            this.licenseStatus = LicenseStatus.MAJOR_RELEASE_INVALID;
        } else {
            this.licenseStatus = LicenseStatus.LICENSE_VALID;
        }

        return this.licenseStatus;
    }

    private Boolean checkLicenseIsVerified() {
        return license.isVerified();
    }

    private Boolean checkDateValid(Date today, Date licenseValidUntil) {
        return licenseValidUntil == null || today.before(licenseValidUntil);
    }

    private Boolean checkProductValid(String myProduct, String licenseProduct) {
        return licenseProduct == null || licenseProduct.equalsIgnoreCase(myProduct);
    }

    private Boolean checkMajorVersionValid(Integer myMajorReleaseNumber, Integer licenseMajorReleaseNumber) {
        return licenseMajorReleaseNumber == null || myMajorReleaseNumber <= licenseMajorReleaseNumber;
    }

}
