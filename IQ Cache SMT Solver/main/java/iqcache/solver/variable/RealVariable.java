package iqcache.solver.variable;
/**
 * Copyright (c) 2009-13
 * The IQCache Team
 * 
 * In case of any questions or comments please feel free to contact
 * Christoph Ehlers ( cehlers@acm.org ).
 *
 * All Rights Reserved.
 *
 * IQCache is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License (GPL)
 * as published by the Free Software Foundation; 
 * either version 3 of the License, or (at your option) any later version.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, you can find the GPL at "http://www.gnu.org/copyleft/gpl.html".
 */
import iqcache.query.column.Column;

/**
 * An abstract variable of type real. This type does not have a size.
 * 
 * @author $LastChangedBy: ehlers $
 * @version $Revision: 2919 $
 * @version $LastChangedDate: 2013-04-26 14:12:54 +0200 (Fri, 26 Apr 2013) $
 */
public abstract class RealVariable extends AbstractVariable {
	
	/**
	 * Construct the variable
	 * 
	 * @param column the corresponding {@link Column},
	 *        {@code column != null}
	 * @throws NullPointerException iff {@code column == null}
	 */
	public RealVariable(Column column) {
		super(column);
	}
	
	@Override
	public String getType() {
		return "real";
	}
	
	@Override
	public boolean hasSize() {
		return false;
	}
	
}
