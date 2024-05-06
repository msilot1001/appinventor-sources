FROM gitpod/workspace-full

USER gitpod

RUN DEBIAN_FRONTEND=noninteractive sdk install java 8.0.412-tem && sdk install maven