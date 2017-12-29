# Darwino JNoSQL Diana Driver

**Darwino**: [Darwino](http://darwino.com) is a Java-based development environment for creating apps that run across servers, desktops, and mobile devices, sharing common code among all platforms. The database component is a NoSQL data store running on top of a number of supported SQL databases.

**JNoSQL Diana**: See [https://github.com/eclipse/jnosql-diana-driver](https://github.com/eclipse/jnosql-diana-driver)

### How To Test

The driver assumes that you are running within a Darwino application. The test environment for the driver instantiates such an application, but it requires access to the Darwino Maven repositories, which in turn require a free Community Edition account from [darwino.com](http://darwino.com). Once this is configured and the dependencies are resolved, the tests will use a local temporary SQLite database.