package dk.tbsalling.aismessages.keymgr;

import net.java.truelicense.core.*;
import net.java.truelicense.json.V2JsonLicenseManagementContext;
import net.java.truelicense.obfuscate.*;

/**
 * A licensing schema for consuming AISmessages v1.X license keys
 * with a Free Trial Period (FTP) of thirty calendar days.
 * A licensing schema defines the algorithms and parameters for the license
 * management of your product.
 * It also holds one or more license consumer managers which are configured
 * according to this definition - one license consumer manager for each feature
 * set.
 * In this sample schema, only one feature set and thus only one license
 * manager is provided.
 *
 * @author Christian Schlichtherle
 */
public final class LicensingSchema {

    public static LicenseConsumerManager manager() {
        return Lazy.cm;
    }

    private static class Lazy {

        @Obfuscate
        static final String SUBJECT = "AISmessages v1.X";

        @Obfuscate
        static final String PUBLIC_KEY_STORE_NAME = "public.ks";

        static final ObfuscatedString PUBLIC_KEY_STORE_PASSWORD =
                new ObfuscatedString(new long[]{0x1b2c4e3ad4fb76cfl, 0xb14824222a24f979l, 0x73d7c206dd2569edl});

        @Obfuscate
        static final String PUBLIC_CERT_ENTRY_ALIAS = "mykey";

        static final ObfuscatedString PBE_PASSWORD =
                new ObfuscatedString(new long[]{0x1b2c4e3ad4fb76cfl, 0xb14824222a24f979l, 0x73d7c206dd2569edl});

        static final int FTP_DAYS = 1;

        @Obfuscate
        static final String FTP_KEY_STORE_NAME = "ftp.ks";

        static final ObfuscatedString FTP_KEY_STORE_PASSWORD =
                new ObfuscatedString(new long[]{ 0x7b4abf4aed98b47al, 0xb1e13b4bc0854bccl}); /* => "test1234" */

        @Obfuscate
        static final String FTP_KEY_ENTRY_ALIAS = "mykey";

        static final ObfuscatedString FTP_KEY_ENTRY_PASSWORD =
                new ObfuscatedString(new long[]{ 0x149d045402a96977l, 0xa448f2162811f378l}); /* => "test1234" */

        static final LicenseConsumerManager cm =
                new V2JsonLicenseManagementContext(SUBJECT)
                        .consumer()
                        .manager()
                        .parent()
                        .keyStore()
                        .loadFromResource(PUBLIC_KEY_STORE_NAME)
                        .storePassword(PUBLIC_KEY_STORE_PASSWORD)
                        .alias(PUBLIC_CERT_ENTRY_ALIAS)
                        .inject()
                        .pbe()
                        .password(PBE_PASSWORD)
                        .inject()
                        .storeInUserNode(LicensingSchema.class)
                        .inject()
                        .ftpDays(FTP_DAYS)
                        .keyStore()
                        .loadFromResource(FTP_KEY_STORE_NAME)
                        .storePassword(FTP_KEY_STORE_PASSWORD)
                        .alias(FTP_KEY_ENTRY_ALIAS)
                        .keyPassword(FTP_KEY_ENTRY_PASSWORD)
                        .inject()
                        .storeInUserNode(sun.security.provider.Sun.class)
                        .build();
    }
}
