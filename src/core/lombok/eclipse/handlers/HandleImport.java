/*
 * Copyright (C) 2011 The Project Lombok Authors.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.eclipse.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import lombok.Import;
import lombok.core.AST.Kind;
import lombok.core.AnnotationValues;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.mangosdk.spi.ProviderFor;

/**
 * Handles the {@code lombok.Import} annotation for eclipse.
 */
@ProviderFor(EclipseAnnotationHandler.class)
public class HandleImport extends EclipseAnnotationHandler<Import> {
	@Override public boolean deferUntilPostDiet() {
		return true;
	}
	
	private static Map<Annotation, Map<String, String>> annotations = new WeakHashMap<Annotation, Map<String, String>>();
	
	@Override public void preHandle(AnnotationValues<Import> annotation, Annotation ast, EclipseNode annotationNode) {
		fillMapAndCleanAnnotation(ast, annotation, annotationNode);
	}
	
	private Map<String, String> fillMapAndCleanAnnotation(Annotation ast, AnnotationValues<Import> values, EclipseNode annotationNode) {
		Map<String, String> map = annotations.get(ast);
		if (map != null) return map;
		
		map = new HashMap<String, String>();
		for (String mName : values.getMethodNames()) map.put(mName, values.getRawExpression(mName));
		MarkerAnnotation newAnn;
		annotationNode.up().replaceChildNode(ast, newAnn = new MarkerAnnotation(ast.type, ast.sourceStart));
		annotations.put(newAnn, map);
		annotations.put(ast, map);
		
		return map;
	}
	
	@Override public void handle(AnnotationValues<Import> annotation, Annotation ast, EclipseNode annotationNode) {
		EclipseNode context = annotationNode.up();
		if (context == null || context.getKind() != Kind.TYPE && context.getKind() != Kind.METHOD) {
			annotationNode.addError("'@Import' is only legal on types and methods.");
			return;
		}
		
		final Map<String, String> map = fillMapAndCleanAnnotation(ast, annotation, annotationNode);
		
		ASTVisitor visitor = new ASTVisitor() {
			public boolean visit(ParameterizedQualifiedTypeReference typeRef, ClassScope scope) {
				return visit(typeRef, (BlockScope) null);
			}
			
			public boolean visit(ParameterizedQualifiedTypeReference typeRef, BlockScope scope) {
				if (typeRef.tokens == null || typeRef.tokens.length == 0) return true;
				String name = new String(typeRef.tokens[0]);
				String repl0 = map.get(name);
				if (repl0 == null) return true;
				
				String[] repl = repl0.split("\\.");
				switch (repl.length) {
				case 0:
					break;
				case 1:
					typeRef.tokens[0] = repl[0].toCharArray();
					break;
				case 2:
					char[][] old = typeRef.tokens;
					typeRef.tokens = new char[old.length + repl.length - 1][];
					System.arraycopy(old, 1, typeRef.tokens, repl.length, old.length - 1);
					for (int i = 0; i < repl.length; i++) {
						typeRef.tokens[i] = repl[i].toCharArray();
					}
					break;
				}
				
				return true;
			}
			
			@Override public boolean visit(QualifiedTypeReference typeRef, ClassScope scope) {
				return visit(typeRef, (BlockScope) null);
			}
			
			public boolean visit(QualifiedTypeReference typeRef, BlockScope scope) {
				if (typeRef.tokens == null || typeRef.tokens.length == 0) return true;
				String name = new String(typeRef.tokens[0]);
				String repl0 = map.get(name);
				if (repl0 == null) return true;
				
				String[] repl = repl0.split("\\.");
				switch (repl.length) {
				case 0:
					break;
				case 1:
					typeRef.tokens[0] = repl[0].toCharArray();
					break;
				case 2:
					char[][] old = typeRef.tokens;
					typeRef.tokens = new char[old.length + repl.length - 1][];
					System.arraycopy(old, 1, typeRef.tokens, repl.length, old.length - 1);
					for (int i = 0; i < repl.length; i++) {
						typeRef.tokens[i] = repl[i].toCharArray();
					}
					break;
				}
				
				return true;
			}
			
			public boolean visit(SingleTypeReference typeRef, ClassScope scope) {
				return visit(typeRef, (BlockScope) null);
			}
			
			public boolean visit(SingleTypeReference typeRef, BlockScope scope) {
				if (typeRef.token == null) return true;
				String name = new String(typeRef.token);
				String repl0 = map.get(name);
				if (repl0 == null) return true;
				
				String[] repl = repl0.split("\\.");
				switch (repl.length) {
				case 0:
					break;
				case 1:
					typeRef.token = repl[0].toCharArray();
					break;
				case 2:
					// Replace STR with QTR.
					break;
				}
				
				return true;
			}
			
			public boolean visit(ArrayTypeReference typeRef, ClassScope scope) {
				return visit(typeRef, (BlockScope) null);
			}
			
			public boolean visit(ArrayTypeReference typeRef, BlockScope scope) {
				if (typeRef.token == null) return true;
				String name = new String(typeRef.token);
				String repl0 = map.get(name);
				if (repl0 == null) return true;
				
				String[] repl = repl0.split("\\.");
				switch (repl.length) {
				case 0:
					break;
				case 1:
					typeRef.token = repl[0].toCharArray();
					break;
				case 2:
					// Replace ATR with QTR.
					break;
				}
				
				return true;
			}
			
			public boolean visit(ParameterizedSingleTypeReference typeRef, ClassScope scope) {
				return visit(typeRef, (BlockScope) null);
			}
			
			public boolean visit(ParameterizedSingleTypeReference typeRef, BlockScope scope) {
				if (typeRef.token == null) return true;
				String name = new String(typeRef.token);
				String repl0 = map.get(name);
				if (repl0 == null) return true;
				
				String[] repl = repl0.split("\\.");
				switch (repl.length) {
				case 0:
					break;
				case 1:
					typeRef.token = repl[0].toCharArray();
					break;
				case 2:
					// Replace PSTR with PQTR.
					break;
				}
				
				return true;
			}
		};
		
		if (context.getKind() == Kind.TYPE) {
			((TypeDeclaration)context.get()).traverse(visitor, (CompilationUnitScope) null);
		} else {
			((AbstractMethodDeclaration)context.get()).traverse(visitor, (ClassScope) null);
		}
	}
}
