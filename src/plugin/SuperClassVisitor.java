package plugin;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SuperClassVisitor extends ASTVisitor{
	public Type 	superClass;
	public String 	newSuperClass;
	private String 	oldSuperClass;
	
	
	public SuperClassVisitor(String newType, String oldType) {
		this.newSuperClass = newType;
		this.oldSuperClass = oldType;
	}

	public boolean visit(TypeDeclaration node) {
		superClass = node.getSuperclassType();
		if (newSuperClass != null) {
			Name oldName = node.getAST().newName(oldSuperClass);
			SimpleType oldType = node.getAST().newSimpleType(oldName);
			
			Name newName = node.getAST().newName(newSuperClass);
			SimpleType newType = node.getAST().newSimpleType(newName);
			
			if (superClass != null && superClass.equals(oldType)) {
				node.setSuperclassType(newType);				
			}
		}
		return true;
	}
} 