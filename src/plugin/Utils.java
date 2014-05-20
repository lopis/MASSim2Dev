package plugin;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Type;

public class Utils {

	public static void dialog(String title, String text, ExecutionEvent event) {
		try {
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(window.getShell(), title, text);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
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


	public static Type getSuperClass(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);

		// Now create the AST for the ICompilationUnits
		CompilationUnit astUnit = (CompilationUnit) parser.createAST(null);
		SuperClassVisitor visitor = new SuperClassVisitor();
		astUnit.accept(visitor);

		return visitor.superClass;
	}
}
