package io.synlabs.synvision.auth;

public final class LicenseServerAuth
{
  private LicenseServerAuth()
  {
    throw new AssertionError("Not allowed");
  }

  public static final class Privileges
  {
    public static final String SELF_READ  = "ROLE_SELF_READ";
    public static final String SELF_WRITE = "ROLE_SELF_WRITE";

    public static final String USER_READ  = "ROLE_USER_READ";
    public static final String USER_WRITE = "ROLE_USER_WRITE";

    public static final String INCIDENT_READ = "ROLE_INCIDENT_READ";
    public static final String INCIDENT_WRITE = "ROLE_INCIDENT_WRITE";

    public static final String DEVICE_READ = "ROLE_DEVICE_READ";

    public static final String ROLE_READ = "ROLE_ROLE_READ";
    public static final String ROLE_WRITE = "ROLE_ROLE_WRITE";

    public static final String FEED_READ = "ROLE_FEED_READ";
    public static final String FEED_WRITE = "ROLE_FEED_WRITE";

  }
}
