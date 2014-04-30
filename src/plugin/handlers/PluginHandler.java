package plugin.handlers;

import massim2dev.J2R;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * This class handles the event from clicking on the tool bar icon.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class PluginHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public PluginHandler() {
	}

	/**
	 * This method is executed when the event is triggered.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		MessageDialog.openInformation(
//				window.getShell(),
//				"Testeplugin",
//				"Hello, Eclipse world");
		J2R j2R = new J2R();
		j2R.convertCurrent(event);
		return null;
	}
}
