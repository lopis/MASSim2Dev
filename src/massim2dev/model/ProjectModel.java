package massim2dev.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * This class contains settings and objects related
 * to the current conversion session. It also contains
 * the structure for the project tree, i.e. the blueprint
 * for all packages and classes that will be generated.
 * @author joaolopes
 *
 */
public class ProjectModel {

	// Regex that matches all API classes
	private static final String PREFFIX_REGEX = "up\\.fe\\.liacc\\.sajas\\.";

	/**
	 * Represents the original project.
	 */
	IProject oldProject;
	IProject newProject;
	private String name;
	IJavaProject newJavaProject;
	private IWorkspaceRoot workspaceRoot;


	// These classes don't have an equivalent in JADE
	// so they are not converted, but removed.
	private String[] restrictedImports = {
			"up/fe/liacc/repacl/ContextWrapper.java",
			"up/fe/liacc/repacl/MailBox.java",
			"repast/RepastAgent.java"
	};

	/**
	 * Contains the absolute system path to the new project folder. 
	 */
	private String newProjectPath;

	public ProjectModel(String name) {
		this.name= name;
	}

	public void setCurrentProject(IProject oldProject) {
		this.oldProject = oldProject;
	}

	/**
	 * Copy the selected project.
	 */
	public void generate() {
		if (oldProject == null) {
			return;
		}

		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		try {

			// Create a handle for the project.
			newProject = workspaceRoot.getProject(this.name);
			newProjectPath = workspaceRoot.getRawLocation().toString() + newProject.getFullPath().toString();

			// Delete the contents of any old project with the same name 
			if (newProject.exists()) {
				newProject.delete(true, true, null);
			}

			// Effectively create the project and open it.
			newProject.create(null);
			newProject.open(null);

			// Set the Java Nature of the project
			IProjectDescription description = newProject.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			newProject.setDescription(description, null);

			// Set the build path
			//  - Add the "src" as a source folder
			//  - Add "jade.jar" as a library and copy that jar from within the plugin
			//  - Add the JRE container too
			newJavaProject = JavaCore.create(newProject);
			String jarName = "jade.jar";
			copyJar(jarName);
			IPath jadeLibPath = new Path(newProjectPath + "/" + jarName);
			IClasspathEntry[] buildPath = {
					JavaCore.newSourceEntry(newProject.getFullPath().append("src")),
					JavaCore.newLibraryEntry(jadeLibPath , null, null),
					JavaRuntime.getDefaultJREContainerEntry()};
			newJavaProject.setRawClasspath(buildPath, newProject.getFullPath().append("bin"), null);

			// Create the src folder
			IFolder srcFolder = newProject.getFolder("src");
			// Copy the src contents from the original project
			IPath destination = srcFolder.getFullPath();
			oldProject.getFolder("src").copy(destination, true, null);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Copy a jar into the project root from the plugin's root.
	 * @param fileName
	 */
	private void copyJar(String fileName) {
		
		String fullpath = newProjectPath + "/" + fileName;

		// Open stream for the jar located in the plugin
		InputStream inputStream =  this.getClass().getClassLoader().
				getResourceAsStream(fileName);
		if (inputStream == null) {
			System.err.println("The resource " + fileName + " could not be found.");
			return;
		}
		File targetFile = new File(fullpath);
		FileOutputStream outputStream;

		try {
			if (targetFile.exists())
				return;
			
			outputStream = new FileOutputStream(targetFile);
			byte[] buf = new byte[1024];
			int i = 0;

			while((i=inputStream.read(buf))!=-1) {
				outputStream.write(buf, 0, i);
			}
			inputStream.close();
			outputStream.close();

		} catch (FileNotFoundException e) {
			System.err.println("File '" + targetFile + "' not found.");
		} catch (IOException e) {
			System.err.println("IOException while writing to '" + targetFile + "'.");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("NullPointerException while reading '" + fileName + "'.");
		}
	}

	/**
	 * Copy a file into a package from the project root.
	 * 
	 * @param fileName The name of the file inside the plugin.
	 * @param destPackage The destination package.
	 */
	private void copyFile(String fileName, String destPackage) {

		// Create the package
		try {
			createPackage(destPackage);
		} catch (JavaModelException e) {
			System.err.println("Failed to create package " + destPackage);
			e.printStackTrace();
		}

		String fullFilePath = newProjectPath + "/src/" + destPackage.replace('.', '/')  + "/" + fileName;
		
		
		// Open stream for the jar located in the plugin
		InputStream inputStream =  this.getClass().getClassLoader().getResourceAsStream(fileName);
		File targetFile = new File(fullFilePath);
		if (targetFile.exists()) {
			return;
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			byte[] buf = new byte[1024];
			int i = 0;

			while((i=inputStream.read(buf))!=-1) {
				outputStream.write(buf, 0, i);
			}
			inputStream.close();
			outputStream.close();

		} catch (FileNotFoundException e) {
			System.err.println("File '" + fullFilePath + "' not found.");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("IOException while reading '" + fileName + "'.");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("NullPointerException while reading '" + fileName + "'.");
		}
	}

	/**
	 * 
	 * @param packageName The given dot-separated package name
	 * @throws JavaModelException
	 */
	private void createPackage(String packageName) throws JavaModelException {
		IJavaProject javaProject = JavaCore.create(newProject);
		IFolder folder = newProject.getFolder("src");
		IPackageFragmentRoot srcFolder = javaProject.getPackageFragmentRoot(folder);
		srcFolder.createPackageFragment(packageName, true, null);
	}

	/**
	 * Parse all classes and replace all API imports
	 * for JADE imports. Original adapted from
	 * http://www.programcreek.com/2012/06/count-total-number-of-methods-in-a-java-project/
	 */
	public void replaceImports() {

		try {
			// load each package
			IPackageFragment[] packages = newJavaProject.getPackageFragments();
			for (IPackageFragment aPackage : packages) {
				if (aPackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					// The compilation unit is a class
					for (ICompilationUnit unit : aPackage.getCompilationUnits()) {
						System.out.println(" class name: " + unit.getElementName());

						replaceImports(unit);
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private void replaceImports(ICompilationUnit unit) throws JavaModelException {
		IImportDeclaration[] imports = unit.getImports();

		for (int i = 0; i < imports.length; i++) {

			String importName = imports[i].getElementName();

			// Import matches up.fe.liacc.repacl ?
			if (importName.matches(PREFFIX_REGEX + ".+")) {
				// Import in ignored imports list?
				if (!isRestricted(importName)) {
					// If not, replace it with its JADE equivalent
					// And then delete the original import.
					String newImport = importName.replaceFirst(PREFFIX_REGEX, "jade.");
					unit.createImport(newImport, imports[i], null);
				}
				imports[i].delete(false, null);


			} else if(importName.matches(".*\\.repast\\.Launcher")) {
				String newImport = "Launcher";
				unit.createImport(newImport, imports[i], null);
				imports[i].delete(false, null);
				copyFile("Launcher.java", "launcher");

				// Import matches repast.+?
			} else if (importName.matches("repast\\..+")) {
				// Remove the import and the class //FIXME
				imports[i].delete(false, null);
			} 


			//			if (unit.getType(elementName).getSuperclassName().equals("Launcher")) {
			//				System.out.println("OMGGGGGGGGGGGGGGGGGGG");
			//				String launcherName = "Launcher.java";
			//				copyFile(launcherName);
			//			}
		}
	}

	private boolean isRestricted(String elementName) {
		for (int i = 0; i < restrictedImports.length; i++) {
			if (elementName.equals(restrictedImports[i])) {
				return true;
			}
		}
		return false;
	}

}
