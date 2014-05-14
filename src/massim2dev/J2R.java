package massim2dev;

import massim2dev.model.ProjectModel;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Main class for code conversion from JADE to Repast. To use it,
 * call the method convertCurrent(...).
 * @author joaolopes
 *
 */
public class J2R {
	
	ProjectModel project;

	/**
	 * Loads the currently selected project and initiates the
	 * conversion process. 
	 */
	public void convertCurrent(ExecutionEvent event) {
		
		String name = "generated-project"; //TODO: prompt the user to define this name
		
		project = new ProjectModel(name);
		
		// Get the selected project. If null, stop execution and ask
		// the user to select a project to be used.
		IProject selectedProject = getCurrentProject();
		
		if (selectedProject == null) {
			System.err.println("No project selected.");
			return;
		}
		
		// Generate project and folders/packages
		project.setCurrentProject(selectedProject);
		project.generate();
		project.replaceImports();
	}
	
	/**
	 * @return The currently selected project in the package explorer.
	 * Returns null if no project is selected. Even if a file inside a project
	 * is selected, the correspondent project ir returned. 
	 */
	public static IProject getCurrentProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			
			// IAdaptable is an eclipse interface that is implemented by
			// lots of other interfaces. Basically what this does is filter
			// any selected object like a class, package, folder, etc in the
			// workbench and returns the project that contains it.
			if (firstElement instanceof IAdaptable) {
				IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
				try {
					// check if we have a Java project
					if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
						// IJavaProject javaProject = JavaCore.create(project); // throws CoreException
						return project;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
}
