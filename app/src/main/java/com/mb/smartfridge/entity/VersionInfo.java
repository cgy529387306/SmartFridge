package com.mb.smartfridge.entity;

public class VersionInfo {
    private String objectId;
    private String updatedAt;
    private String className;
    private ServerDataBean serverData;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ServerDataBean getServerData() {
        return serverData;
    }

    public void setServerData(ServerDataBean serverData) {
        this.serverData = serverData;
    }

    public static class ServerDataBean {
        private String force_reboot;
        private String os;
        private String tips;
        private String title;
        private String version;

        public String getForce_reboot() {
            return force_reboot;
        }

        public void setForce_reboot(String force_reboot) {
            this.force_reboot = force_reboot;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
