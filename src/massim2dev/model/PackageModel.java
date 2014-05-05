package massim2dev.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * This class represents a package in a Java project.
 * It contains a list of packages and classes and implements
 * the necessary methods to generate the code of all these
 * classes and the correct package structure.
 * @author joaolopes
 *
 */
public class PackageModel extends ModelGenerator {

	/**
	 * Contains all packages and classes.
	 * All objects in this array implement the method
	 * generate() which creates the code, directories
	 * and projects.
	 */
	ModelGenerator[] packageContents;
	private IPackageFragment thisPackage;

	public PackageModel() {

	}

	public PackageModel(String elementName, ProjectModel project) {
		this.setName(elementName);
		this.setCurrentProject(project);
	}

	@Override
	public void generate() {
		createPackage(getProject());
	}

	public void setPackageContents(ModelGenerator[] packageContents) {
		this.packageContents = packageContents;
	}

	private void createPackage(ProjectModel project) {
		IFolder srcFolder = project.newProject.getFolder("src");

		try {
			if (!srcFolder.exists()) {
				srcFolder.create(true, true, null);
			}
			IPackageFragmentRoot srcFragment = project.newJavaProject.getPackageFragmentRoot(srcFolder);
			thisPackage =  srcFragment.createPackageFragment(this.getName(), true, null);
			generateClasses();
			
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void generateClasses() {
		try {
			IJavaElement[] children = this.thisPackage.getChildren();
			System.out.println(children);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
