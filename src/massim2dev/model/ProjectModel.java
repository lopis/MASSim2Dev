package massim2dev.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

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
	IProject oldProject;
	IProject newProject;
	IJavaProject newJavaProject;

	public ProjectModel(String name) {
		this.setName(name);	
	}

	public void setCurrentProject(IProject oldProject) {
		this.oldProject = oldProject;
	}

	@Override
	/**
	 * Copy the selected project.
	 */
	public void generate() {
		if (oldProject == null) {
			return;
		}
		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		try {
			
			// Create a handle for the project.
			newProject = workspaceRoot.getProject(this.getName());
			
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
			
			// Set the build path and the src folder
			IJavaProject javaProject = JavaCore.create(newProject);
			IClasspathEntry[] buildPath = {
					JavaCore.newSourceEntry(newProject.getFullPath().append("src"))};
			javaProject.setRawClasspath(buildPath, newProject.getFullPath().append(
					"bin"), null);
			
			// Create the src folder
			IFolder srcFolder = newProject.getFolder("src");
			// Copy the src contents from the original project
			IPath destination = srcFolder.getFullPath();
			oldProject.getFolder("src").copy(destination, true, null);
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
