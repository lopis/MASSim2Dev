package plugin;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SuperClassVisitor extends ASTVisitor{
	public Type superClass;
	
	public SuperClassVisitor() {}

	public boolean visit(TypeDeclaration node) {
		superClass = node.getSuperclassType();
		System.out.println(superClass);
		return true;
	}
}