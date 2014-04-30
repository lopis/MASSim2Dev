package massim2dev.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
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
			IPackageFragment fragment = srcFragment.createPackageFragment(this.getName(), true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}


}
