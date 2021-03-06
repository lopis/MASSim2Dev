package plugin.handlers;

import massim2dev.model.Dictionary;
import massim2dev.model.SAJaSProjectModel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;

import plugin.Utils;

/**
 * This class handles the event from clicking on the tool bar icon [JS].
 * It handles the conversion of a JADE-based project to a SAJaS one.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class JSHandler extends AbstractHandler {
	
	private SAJaSProjectModel project;

	/**
	 * The constructor.
	 */
	public JSHandler() {
	}

	/**
	 * This method is executed when the event is triggered.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if(!Dictionary.loadDictionaryFile("dictionary"))
			return null;
		
		// Get the selected project. If null, stop execution and ask
		// the user to select a project to be used.
		IProject selectedProject = Utils.getCurrentProject();
		if (selectedProject == null) {
			Utils.dialog("No project selected.",
					"Please select a SAJaS-based project and try again.",
					event);
			return null;
		}
		
		project = new SAJaSProjectModel(selectedProject);
		System.out.println("Selected Project: " + selectedProject.getName());

		// Clone the project and packages
		project.cloneProject();
		// Perform transformations
		project.replaceImports();
		
		return null;
	}
}
