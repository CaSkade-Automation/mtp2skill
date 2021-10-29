<p align="center">
    <img width="500px" src="https://github.com/hsu-aut/mtp2skill/blob/documentation/images/documentation/images/Mtp2Skill-icon.png?raw=true">
</p>
<h1 align="center">Automatically Generate Semantic Skills<br> from a Module Type Package</h1>
<br>

An automated mapping approach to transform a Module Type Package (MTP) into an ontological skill model. This skill model consists of several individual ontologies which are all based on industry standards. You can find the skill model [in this repository](https://github.com/aljoshakoecher/Machine-Skill-Model).
MTP2Skill makes use of [RML mapping rules](https://rml.io/specs/rml/) to transfer AutomationML elements of an MTP into individuals and relations of a skill ontology. Written in Java, MTP2Skill can be used in three different ways.

## Usage:
You can use MTP2Skill locally with a command line interface (CLI), run it as a web-service or integrate it into your application as a library.

### CLI
Download the current `mtp2skill-cli-x.x.x-jar-with-dependencies.jar` from the releases into a folder of your choice. Inside that folder, open a shell and execute `java -jar mtp2skill-cli-x.x.x-jar-with-dependencies.jar -f <Name of the file you want to map>`. The mapping result will be written to a file right next to the cli.jar.

### REST-API
Download the current `mtp2skill-rest-api-x.x.x-jar-with-dependencies.jar` from the releases into a folder of your choice and from a shell, run `java -jar mtp2skill-rest-api-x.x.x-jar-with-dependencies.jar`. This will start a web server and you can send HTTP POST request to `localhost:9191` to invoke the mapper. When creating the request, make sure to set the `Content-Type` header to `multipart/form-data`. Furthermore, you have to send the file with form key / name "mtp-file".

### Library
You can also include the library which is used in both the CLI-application and REST API in your own projects. In order to do so, import `MtpToSkillMapper` and after obtaining a new instance of the mapper, use `executeMapping(<Path to your MTP file>)`.<br>
:construction:In the future, we might publish this library to Maven central for easier integration into Maven projects :construction:
