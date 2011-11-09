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
