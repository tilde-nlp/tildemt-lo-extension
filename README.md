# LibreOffice Tilde MT Extension

This Libre Office extension is based on this starter: [LibreOffice Starter Extension](https://github.com/smehrbrodt/libreoffice-starter-extension)

This extension provides functionality to translate Libre Office documents with `Tilde MT`.

## Get started

1. Install [LibreOffice](http://www.libreoffice.org/download) & the [LibreOffice SDK](http://www.libreoffice.org/download) (5.0 or greater)
2. Install [Eclipse](http://www.eclipse.org/) IDE for Java Developers & the [LOEclipse plugin](https://marketplace.eclipse.org/content/loeclipse)
3. [Download](https://github.com/smehrbrodt/libreoffice-starter-extension/archive/master.zip) this starter project & unzip it
4. Import the project in Eclipse (File->Import->Existing Projects into Workspace)
5. Let Eclipse know the paths to LibreOffice & the SDK (Project->Properties->LibreOffice Properties)
6. Setup Run Configuration
    * Go to Run->Run Configurations
    * Create a new run configuration of the type "LibreOffice Application"
    * Select the project
    * Run!
    * *Hint: Show the error log to view the output of the run configuration (Window->Show View->Error Log)*
7. The extension will be installed in LibreOffice (see Tools->Extension Manager)
8. To translate ... TODO: describe this.

## Debugging tips and tricks

* `Eclipse` / `Java` knows how to autopatch code in live, so if you change `*.java` when not in break mode, you shall automatically see changes in plugin. 
* If `Eclipse` is showing that program is terminated, that can be caused by: 
  - Yo're trying to run `debug option` when starting project more than twice on the same code base => just restart `Eclipse`, should be fine first time after restart. 
  - compilation errors: [loeclipse Issue 14](https://github.com/LibreOffice/loeclipse/issues/14)
  - Libre Office is running => terminate in task manager
  - There is some kind of bug in code but Eclipse is not showing errors like for example in `/registry/org/openoffice/Office/Accelerators.xcu`
  - Some random `Eclipse` / `loeclipse` bug 
    - restart Eclipse :)
    - recreate `eclipse-workspace` settings? 

## Development Hints
* Project entry point configuration is located in `./.unoproject` there is option `regclassname` 
* The entry point is in [TildeTranslatorImpl.java](./source/com/tilde/mt/lotranslator/comp/TildeTranslatorImpl.java).
* Toolbar items and menu entries are defined in [Addons.xcu](./registry/org/openoffice/Office/Addons.xcu).
* Keyboard shortcuts for plugin actions are defined in [Accelerators.xcu](./registry/org/openoffice/Office/Accelerators.xcu).
* The position of the toolbar is defined in [WriterWindowState.xcu](./registry/org/openoffice/Office/UI/WriterWindowState.xcu).
* To debug the Java code, just stick a breakpoint anywhere in Eclipse and start your run configuration in debug mode.
* If you add non-code files (or an external .jar) to your extension, you need to mention them in [package.properties](./package.properties), else they won't be included in the packaged extension.
* Now go on customizing the extension to your needs. Some helpful links:
  * [OpenOffice Wiki](https://wiki.openoffice.org/wiki/Extensions_development)
  * [API Reference](http://api.libreoffice.org/docs/idl/ref/index.html)
  * [Example extensions](http://api.libreoffice.org/examples/examples.html#Java_examples)


## Distribution / Testing

* Built extension is located in `/dist/*.oxt`. You can double click it and it should automatically open install wizard in Libre Office.
