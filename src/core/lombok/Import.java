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
package lombok;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put on any method or type to create an import alias that can be used within that method or type.
 * 
 * Example:
 * <pre>
 *     &#64;Import(AList = java.awt.List, UList = java.util.List, swt = org.eclipse.swt.widgets)
 *     public class Test {
 *         private UList<swt.Button> swtButtons = new ArrayList<swt.Button>();
 *         
 *         public swt.Button getFirst() {
 *             AList listWidget = (AList) getWidget();
 *             if (listWidget != null) return toButton(listWidget);
 *             return swtButtons.get(0);
 *         }
 *     }
 * </pre>
 * 
 * will replace all occurrences of {@code UList} with {@code java.util.List}, {@code swt.Button} with
 * {@code org.eclipse.swt.widgets.Button}, and {@code AList} with {@code java.awt.List}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Import {
}
