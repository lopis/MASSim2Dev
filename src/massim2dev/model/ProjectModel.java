package massim2dev.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * This class contains settings and objects related
 * to the current conversion session. It also contains
 * the structure for the project tree, i.e. the blueprint
 * for all packages and classes that will be generated.
 * @author joaolopes
 *
 */
public class ProjectModel extends ModelGenerator {

	/**
	 * Represents the original project.
	 */
	IProject currentProject;
	IProject newProject;
	IJavaProject newJavaProject;

	public ProjectModel(String name) {
		this.setName(name);	
	}

	public void setCurrentProject(IProject currentProject) {
		this.currentProject = currentProject;
	}

	@Override
	public void generate() {
		if (currentProject == null) {
			return;
		}

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		try {
			newProject = workspaceRoot.getProject(this.getName());
			newProject.create(null);
			newProject.open(null);
		} catch (CoreException e) {
			System.err.println("[ProjectModel] Could not open new project '" + this.getName() + "'.");
			System.err.println(e.getMessage());
			//e.printStackTrace();
			return;
		}
		IProjectDescription description;
		try {
			description = newProject.getDescription();

			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			newProject.setDescription(description, null);
			newJavaProject = JavaCore.create(currentProject);
		} catch (CoreException e) {
			System.err.println("[ProjectModel] Could not set project description '" + this.getName() + "'.");
			System.err.println(e.getMessage());
			//e.printStackTrace();
		}




		// Create package directories
		PackageModel[] packages = generatePackageModels();

	}

	private PackageModel[] generatePackageModels() {
		PackageModel[] packageModels = new PackageModel[0];
		try {
			IPackageFragment[] packages = newJavaProject.getPackageFragments();
			packageModels = new PackageModel[packages.length];
			System.out.println("Generating packages...");

			for (int i = 0; i < packages.length; i++) {
				// Package fragments include all packages in the classpath.
				// We will only look at the package from the source folder.
				// K_BINARY would include also included Jars
				if (packages[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
					System.out.println("[" + (i) + "/" + packages.length + "] " + packages[i].getElementName());
					packageModels[i] = new PackageModel(packages[i].getElementName(), this);
					packageModels[i].generate();
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return packageModels;
	}

	public IProject getCurrentProject() {
		return this.currentProject;
	}


}
