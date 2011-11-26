/*******************************************************************************
 * 2011 Ivan Shubin http://mindengine.net
 * 
 * This file is part of MindEngine.net Oculus Grid.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Oculus Experior.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.mindengine.oculus.grid.agent;



public class UploadProjectData {

	private String path;
	private String version;
	
	public UploadProjectData(){
	}
	
	public UploadProjectData(String path, String version) {
	    super();
	    this.path = path;
	    this.version = version;
    }
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == path ? 0 : path.hashCode());
		hash = 31 * hash + (null == version ? 0 : version.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object that) {
		if (that != null && that instanceof UploadProjectData) {
			UploadProjectData thatup = (UploadProjectData)that;
			if (path.equals(thatup.getPath()) && version.equals(thatup.getVersion())) {
				return true;
			}
		}
		return false;
	}
	
}
