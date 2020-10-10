package org.fxbench.ui.docking.util;

/**
 * This class determines the version of the Java Virtual Machine.
 * 
 * @author Heidi Rakels.
 */
public class JvmVersionUtil
{
	
	// Static fields.
	
	/** The integer for java 1.3 versions of the JVM. */
	public static final int VERSION_3_OR_LESS = 3;
	/** The integer for java 5 versions of the JVM. */
	public static final int VERSION_4_or_5 = 4;
	/** The integer for java 6 versions of the JVM. */
	public static final int VERSION_6_OR_MORE = 6;
	
	/** The float for java 4 versions of the JVM. */
	private static final float VERSION_4 = 1.4f;
	/** The integer for java 6 versions of the JVM. */
	private static final float VERSION_6 = 1.6f;

	
	// Public static methods.
	
	/**
	 * Gets the version of the JVM. 
	 * 
	 * @return		The integer that corresponds with the version of the JVM. This can be 
	 * 				VERSION_3_OR_LESS,VERSION_4_or_5, VERSION_6_OR_MORE.
	 * @throws 		IllegalStateException 	If the system property <code>java.version</code> does not start with
	 * 										a x.x. x should be a number.
	 */
	public static int getVersion() 
	{
		
		// Get the system property java.vm.version.
		String jVersion = System.getProperty("java.version");
		
		// Get the version number.
		int pointPosition = jVersion.indexOf(".");
		if (pointPosition > 0)
		{
			if (jVersion.length() >= pointPosition + 2)
			{
				String version = jVersion.substring(0, pointPosition + 2);

				try
				{
					float versionNumber = Float.parseFloat(version);
					if (versionNumber < VERSION_4)
					{
						return VERSION_3_OR_LESS;
					}
					if (versionNumber < VERSION_6)
					{
						return VERSION_4_or_5;
					}
					else
					{
						return VERSION_6_OR_MORE;
					}
				}
				catch (NumberFormatException exception)
				{
					throw new IllegalStateException("Unknown java version ["+ jVersion + "].");
				}
			}
		}
		
		throw new IllegalStateException("Unknown java version ["+ jVersion + "].");
		
	}
	
	// Test.
	
//	public static void main(String[] args) {
//		
//		String version = System.getProperty("java.vm.version");
//		System.out.println("Version number: " + getVersion());
//		System.out.println("Version string: " + version);
//		
//	}
	
	// Private constructor.
	
	private JvmVersionUtil()
	{
	}
}
