# Change Log

## [0.9.1](https://github.com/angelozerr/lsp4xml/milestone/11?closed=1) (October 17, 2019)

### Bug Fixes

 * XSD: IntelliSense and element substitutions. See [#568](https://github.com/angelozerr/lsp4xml/pull/568)
 * Completion doesn't use file cache for included XML schema. See [#570](https://github.com/angelozerr/lsp4xml/pull/570)
 * Prevent from NPE validation with schemaLocaton and "schema.reference.4" error. See [#569](https://github.com/angelozerr/lsp4xml/pull/569)

### Performance

 * Improve performance and memory for validation by caching XML Schema / DTD. See [#534](https://github.com/angelozerr/lsp4xml/issues/534)

### Others

 * Update lsp4j version to 0.8.1. See [#571](https://github.com/angelozerr/lsp4xml/pull/571)
 * Reject download of resource which are not in the cache folder. Fixes [CVE-2019-18212](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2019-18212). See [#567](https://github.com/angelozerr/lsp4xml/pull/567)
 * Add disallowDocTypeDecl & resolveExternalEntities validation settings. Fixes [CVE-2019-18213](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2019-18213). See [#566](https://github.com/angelozerr/lsp4xml/pull/566)

## [0.9.0](https://github.com/angelozerr/lsp4xml/milestone/10?closed=1) (September 10, 2019)

### Enhancements

 * Add support for `textDocument/documentHighlight` for DTD. See [#545](https://github.com/angelozerr/lsp4xml/issues/545)
 * Ability to `rename` a `complexType/@name` inside XML Schema. See [#454](https://github.com/angelozerr/lsp4xml/issues/454)
 * Add support for `textDocument/codeLens` for XML DTD. See [#252](https://github.com/angelozerr/lsp4xml/issues/252)
 * Add support for `textDocument/references` for DTD. See [#234](https://github.com/angelozerr/lsp4xml/issues/234)
 * Add support for `textDocument/definition` for DTD. See [#233](https://github.com/angelozerr/lsp4xml/issues/233)

### Bug Fixes

 * Cache completion based on XML Schema/DTD. See [#547](https://github.com/angelozerr/lsp4xml/issues/547)
 * Fix error range for `cvc-datatype-valid-1-2-1`. See [#323](https://github.com/angelozerr/lsp4xml/issues/323)
 * Support completion with `xs:any`. See [#177](https://github.com/angelozerr/lsp4xml/pull/563)
 * Fixes issue with error messages not showing. See [#557](https://github.com/angelozerr/lsp4xml/pull/557)
 * Validation Error Message Fails on Certain Cases. See [#553](https://github.com/angelozerr/lsp4xml/issues/553)
 * Error range for `RootElementTypeMustMatchDoctypedecl`. See [#537](https://github.com/angelozerr/lsp4xml/issues/537)

# Change Log

## [0.8.0](https://github.com/angelozerr/lsp4xml/milestone/9?closed=1) (July 23, 2019)

### Enhancements

 * `Markdown` support for `hover` documentation. See [#24](https://github.com/angelozerr/lsp4xml/issues/245)
 * `Markdown` support for `completion` documentation. See [#526](https://github.com/angelozerr/lsp4xml/issues/526)
 * Add completion for `comment` and `#region`. See [#54](https://github.com/angelozerr/lsp4xml/issues/54)
 * Add completion for `CDATA` block. See [#168](https://github.com/redhat-developer/vscode-xml/issues/168)
 * Find definition for start/end tag element. See [#535](https://github.com/angelozerr/lsp4xml/issues/535)
 * Show `relevant XML` completion options based on XML Schema. See [#347](https://github.com/angelozerr/lsp4xml/issues/347) 
 * Improve `XSD source` information for XML completion. See [#529](https://github.com/angelozerr/lsp4xml/issues/529)
 * Add support for `textDocument/documentHighlight` for XML Schema types. See [#470](https://github.com/angelozerr/lsp4xml/issues/470)
 * Add support for `textDocument/completion` for xs:element/@name / xs:extension/@base. See [#451](https://github.com/angelozerr/lsp4xml/issues/451)
 * Add support for selective outline enablement per file. See [#427](https://github.com/angelozerr/lsp4xml/issues/427)
 * Parse `.ent` and `.mod` files as DTD files. See [#380](https://github.com/angelozerr/lsp4xml/issues/380)
 * Add support for `textDocument/typeDefinition` from XML to XMLSchema/DTD. See [#371](https://github.com/angelozerr/lsp4xml/issues/371)
 * Add support for `textDocument/definition` for XML Schema. See [#148](https://github.com/angelozerr/lsp4xml/issues/148)
 * Add support for `textDocument/references` for XML Schema types. See [#58](https://github.com/angelozerr/lsp4xml/issues/58)
 * Add support for `textDocument/codelens` for XML Schema types. See [#55](https://github.com/angelozerr/lsp4xml/issues/55)
 * Add support for clickable`XSD CodeLens`. See [#490](https://github.com/angelozerr/lsp4xml/issues/490)
 * Improved XML validation when XSD files are saved. See [#506](https://github.com/angelozerr/lsp4xml/issues/506)
 
### Bug Fixes

 * Hover markup response ignored the hover client capability. See [#525](https://github.com/angelozerr/lsp4xml/issues/525)
 * Completion capability was lost in specific scenarios. See [#522](https://github.com/angelozerr/lsp4xml/issues/522)
 * Fixed NPE in `textDocument/definition` in XSD files. See [#488](https://github.com/angelozerr/lsp4xml/issues/488) 
 * Fixed case sensitivity problems for element and attribute names. See [#433](https://github.com/angelozerr/lsp4xml/issues/433)
 * Selection formatting ignores attribute indentation preference. See [#429](https://github.com/angelozerr/lsp4xml/issues/429)
 * Fixed error range for `EntityNotDeclared`. See [#518](https://github.com/angelozerr/lsp4xml/issues/518)
 * Fixed error range for `src-import.1.2`. See [#499](https://github.com/angelozerr/lsp4xml/issues/499)
 * Fixed error range for `s4s-elt-invalid-content.3`. See [#496](https://github.com/angelozerr/lsp4xml/issues/496)
 * Fixed error range for `cvc-pattern-valid`. See [#477](https://github.com/angelozerr/lsp4xml/issues/477)
 * Fixed error range for `AttributePrefixUnbound`. See [#476](https://github.com/angelozerr/lsp4xml/issues/476)
 * Fixed error range for `EmptyTargetNamespace`. See [#472](https://github.com/angelozerr/lsp4xml/issues/472)
 * Fixed error range for `ct-props-correct.3`. See [#467](https://github.com/angelozerr/lsp4xml/issues/467)
 * Fixed error range for `sch-props-correct.2`. See [#462](https://github.com/angelozerr/lsp4xml/issues/462)
 * Fixed error range for `s4s-elt-must-match.2`. See [#458](https://github.com/angelozerr/lsp4xml/issues/458)
 * Fixed error range for `ct-props-correct.3`. See [#455](https://github.com/angelozerr/lsp4xml/issues/455)
 * Fixed error range for `src-ct.1`. See [#453](https://github.com/angelozerr/lsp4xml/issues/453)
 * Fixed error range for `duplicate attribute`.. See [#452](https://github.com/angelozerr/lsp4xml/issues/452)
 * Fixed error range for `p-props-correct.2.1`. See [#436](https://github.com/angelozerr/lsp4xml/issues/436)
 * Fixed error range for `cos-all-limited.2`. See [#428](https://github.com/angelozerr/lsp4xml/issues/428)
 * Fixed error range for `src-element.3`. See [#420](https://github.com/angelozerr/lsp4xml/issues/420)
 * Documents with an Internal Subset DOCTYPE had stopped trying to bind. See [#379](https://github.com/angelozerr/lsp4xml/issues/379)
 * Fixed discrepancy in completion between prefixed and default namespaces. See [#311](https://github.com/angelozerr/lsp4xml/issues/311)
 * XML did not validate when bounded DTD file was not found. See [#167](https://github.com/redhat-developer/vscode-xml/issues/167)
 * Formatter inserts spaces in empty lines. See [#157](https://github.com/redhat-developer/vscode-xml/issues/157)
 * VSCode was not revalidating XML files when relevant XSD files were modified outside VSCode. See [#131](https://github.com/redhat-developer/vscode-xml/issues/131)

### Performance

 * Improve XML Scanner performance. See [#444](https://github.com/angelozerr/lsp4xml/issues/444)
 * Use CompletableFuture to load DOMDocument. See [#439](https://github.com/angelozerr/lsp4xml/issues/439)
 * Examined memory usage. See [#438](https://github.com/angelozerr/lsp4xml/issues/438)
 * Improved `TextDocument` update (in async) performance with `TreeLineTracker`. See [#426](https://github.com/angelozerr/lsp4xml/issues/426)
 * Tested large files for performance. See [#48](https://github.com/angelozerr/lsp4xml/issues/48)

## [0.7.0](https://github.com/angelozerr/lsp4xml/milestone/8?closed=1) (June 11, 2019)

### Enhancements

* Display Java runtime used to launch the server. See [#415](https://github.com/angelozerr/lsp4xml/pull/415).
* Added `xml.symbols.enabled` preference, to enable/disable Document Symbols. See [#413](https://github.com/angelozerr/lsp4xml/issues/413).
* File completion in attribute value. See [#345](https://github.com/angelozerr/lsp4xml/issues/345).
* Validation for an XML Schema. See [#190](https://github.com/angelozerr/lsp4xml/issues/190).
* Ability for XML Prolog completion in DTD files. See [#267](https://github.com/angelozerr/lsp4xml/issues/267).
* Ability to rename a namespace/namespace renaming improvements. See [#366](https://github.com/angelozerr/lsp4xml/issues/366).
* Startup time for SVG DTD file completion was too slow. See [#397](https://github.com/angelozerr/lsp4xml/issues/397).
* Mark element source coming from XML Schema/DTD for completion. See [#210](https://github.com/angelozerr/lsp4xml/issues/210).


### Bug Fixes

* Memory usage improvements. See [#389](https://github.com/angelozerr/lsp4xml/issues/389).
* Fix completion source crash on Windows OS. See [#408](https://github.com/angelozerr/lsp4xml/pull/408).
* Fix error range for `ETagRequired`. See [#387](https://github.com/angelozerr/lsp4xml/issues/387).
* Fix error range for `cos-all-limited.2`. See [#407](https://github.com/angelozerr/lsp4xml/issues/407).
* Fix `normalizePath` test for Windows OS. See [#399](https://github.com/angelozerr/lsp4xml/pull/399).
* Document Symbols only returns the 1st `ATTLIST` value. See [#265](https://github.com/angelozerr/lsp4xml/issues/265).
* Completion in SVG DTD file proposed duplicate completions. See [#386](https://github.com/angelozerr/lsp4xml/issues/386).
* Fixed formatting range issues. See [#76](https://github.com/angelozerr/lsp4xml/issues/76).

## [0.6.0](https://github.com/angelozerr/lsp4xml/milestone/6?closed=1) (May 22, 2019)

### Enhancements

* Attribute completion for both `xsi:schemaLocation` and `xsi:noNamespaceSchemaLocation` are independent of each other. See [#382](https://github.com/angelozerr/lsp4xml/pull/382).
* Upgraded to lsp4j version 0.7.1. See [#370](https://github.com/angelozerr/lsp4xml/issues/370).
* Preference `xml.format.preservedNewLines` to preserve new lines on format. See [#350](https://github.com/angelozerr/lsp4xml/issues/350).

### Bug Fixes

* Fixed error range for `cvc-complex-type.2.4.f`. See [#368](https://github.com/angelozerr/lsp4xml/issues/368).
* Fixed error range for `SchemaLocation` warning. See [#343](https://github.com/angelozerr/lsp4xml/issues/343).
* Fixed error range for `MarkupEntityMismatch`. See [#367](https://github.com/angelozerr/lsp4xml/issues/367).
* Missing schema would generate too many/redundant warnings. See [#336](https://github.com/angelozerr/lsp4xml/issues/336).
* Self-closing tag did not remove end tag if tag name contained uppercase characters. See [#354](https://github.com/angelozerr/lsp4xml/issues/354).
* Placing a `/` in an attribute value triggered autoclosing. See [vscode-xml#126](https://github.com/redhat-developer/vscode-xml/issues/126).
* New Maven POM attribute was breaking tests. See [#356](https://github.com/angelozerr/lsp4xml/pull/356).
* Removed unused settings for testing. See [#356](https://github.com/angelozerr/lsp4xml/pull/378).


## [0.5.1](https://github.com/angelozerr/lsp4xml/milestone/7?closed=1) (April 08, 2019)

### Bug Fixes

* Fixed incorrect expansion of the `~` directory on Windows, for `xml.server.workDir`. See [#348](https://github.com/angelozerr/lsp4xml/pull/348).


## [0.5.0](https://github.com/angelozerr/lsp4xml/milestone/5?closed=1) (April 05, 2019)

### Enhancements

* More detailed completion for Prolog. See [#155](https://github.com/angelozerr/lsp4xml/issues/155).
* Added completion for xmlns attribute. See [#208](https://github.com/angelozerr/lsp4xml/issues/208).
* Have value completion for `xmlns:xsi`. See [#326](https://github.com/angelozerr/lsp4xml/issues/326).
* Make ParentProcessWatcher optional. See [#328](https://github.com/angelozerr/lsp4xml/issues/328).
* Autoclose self-closing tags. See [#239](https://github.com/angelozerr/lsp4xml/issues/239).
* Don't autoclose tag if the closing tag already exists. See [#314](https://github.com/angelozerr/lsp4xml/issues/314).
* Changing the content of an XML Schema triggers validation. See [#213](https://github.com/angelozerr/lsp4xml/issues/213).
* Preference `xml.server.workDir` to set schema cache folder. See [#222](https://github.com/angelozerr/lsp4xml/issues/222).
* Code action to close missing quotes for attributes. See [#137](https://github.com/angelozerr/lsp4xml/issues/137).
* Hover for attribute value documentation from XSD's. See [#12](https://github.com/angelozerr/lsp4xml/issues/12).
* Autocompletion for `xsi:nil` values. See [#247](https://github.com/angelozerr/lsp4xml/issues/247).

### Bug Fixes

* `textDocument/publishDiagnostics` failed with message: Illegal argument: line must be non-negative. See [#157](https://github.com/angelozerr/lsp4xml/pull/157).
* XSI completion item messages were incorrect. See [#296](https://github.com/angelozerr/lsp4xml/issues/296).
* Removed trailing whitespace from normalized strings on format. See [#300](https://github.com/angelozerr/lsp4xml/pull/300).
* Format of attribute without value loses data. See [#294](https://github.com/angelozerr/lsp4xml/issues/294).
* Cleaned up skipped unit tests. See [#319](https://github.com/angelozerr/lsp4xml/issues/319).
* Verified that logger settings were actually set on startup before updating settings. See [#81](https://github.com/angelozerr/lsp4xml/issues/81).
* Fixed error range of cvc-type.3.1.2. See [#318](https://github.com/angelozerr/lsp4xml/issues/318).
* Fixed error range of ETagUnterminated. See [#317](https://github.com/angelozerr/lsp4xml/issues/317).
* Fixed error range of cvc-elt.3.2.1. See [#321](https://github.com/angelozerr/lsp4xml/issues/321).
* Multiple `'insert required attribute'` code actions shown when multiple attributes are missing. See [#209](https://github.com/angelozerr/lsp4xml/issues/209).
* Self closing tag causes NPE in `cvc_complex_type_2_1CodeAction.doCodeAction`. See [#339](https://github.com/angelozerr/lsp4xml/issues/339).
* Closing CDATA tag throws exception. See [#341](https://github.com/angelozerr/lsp4xml/issues/341).
* Fix formatting issue with processing instruction attributes. See [#331](https://github.com/angelozerr/lsp4xml/issues/331).
  
## [0.4.0](https://github.com/angelozerr/lsp4xml/milestone/4?closed=1) (March 07, 2019)

### Enhancements

* Modified schema validation messages. See [#181](https://github.com/angelozerr/lsp4xml/issues/181).
* Preference `xml.format.quotations` to set single vs double quotes for attribute values on format. See [#263](https://github.com/angelozerr/lsp4xml/issues/263).
* Preference `xml.format.preserveEmptyContent` to preserve a whitespace value in an element's content. See [#273](https://github.com/angelozerr/lsp4xml/issues/273).
* Compatibility with OSGi and p2. See [#288](https://github.com/angelozerr/lsp4xml/issues/288).

### Bug Fixes

* Fixed memory leak of file handles. See [#303](https://github.com/angelozerr/lsp4xml/pull/303).
* XSI completion item messages were incorrect. See [#296](https://github.com/angelozerr/lsp4xml/issues/296).
* Removed trailing whitespace from normalized strings on format. See [#300](https://github.com/angelozerr/lsp4xml/pull/300).
* Format of attribute without value loses data. See [#294](https://github.com/angelozerr/lsp4xml/issues/294).

## [0.3.0](https://github.com/angelozerr/lsp4xml/milestone/3?closed=1) (January 28, 2019)

### Enhancements

* Addded root element 'xml' to preferences JSON. See [#257](https://github.com/angelozerr/lsp4xml/issues/257).
* Added ability to format DTD/DOCTYPE content. See [#268](https://github.com/angelozerr/lsp4xml/issues/268).
* Added outline for DTD elements. See [#226](https://github.com/angelozerr/lsp4xml/issues/226).
* Ability to start the server in socket mode. See [#259](https://github.com/angelozerr/lsp4xml/pull/259).
* XML completion based on internal DTD. See [#251](https://github.com/angelozerr/lsp4xml/issues/251).
* XML completion based on external DTD. See [#106](https://github.com/angelozerr/lsp4xml/issues/106).
* Completion for DTD <!ELEMENT, <!ATTRIBUTE, ... . See [#232](https://github.com/angelozerr/lsp4xml/issues/232).
* Provide automatic completion/validation in catalog files. See [#204](https://github.com/angelozerr/lsp4xml/issues/204).
* Hover for XSI attributes. See [#164](https://github.com/angelozerr/lsp4xml/issues/164).
* Show attribute value completion based on XML Schema/DTD. See [#242](https://github.com/angelozerr/lsp4xml/issues/242).
* Added `xml.format.spaceBeforeEmptyCloseTag` preference to insert whitespace before closing empty end-tag. See [#197](https://github.com/angelozerr/lsp4xml/issues/197).
* Completion for XSI attributes. See [#163](https://github.com/angelozerr/lsp4xml/issues/163).
* Changing the content of catalog.xml refreshes the catalogs and triggers validation. See [#212](https://github.com/angelozerr/lsp4xml/issues/212).
* Switched to lsp4j 0.6.0 release. See [#254](https://github.com/angelozerr/lsp4xml/issues/254).
* Added `xml.validation.noGrammar` preference, to indicate document won't be validated. See [#218](https://github.com/angelozerr/lsp4xml/issues/218).
* Added preference to enable/disable validation `xml.validation.enabled` and `xml.validation.schema`. See [#260](https://github.com/angelozerr/lsp4xml/issues/260).
* Deploy lsp4xml to a public Maven repository. [#229](https://github.com/angelozerr/lsp4xml/issues/229).

### Bug Fixes

* Formatting unclosed tag would be in wrong location. See [#269](https://github.com/angelozerr/lsp4xml/issues/269).
* Formatting removes DOCTYPE's public declaration. See [#250](https://github.com/angelozerr/lsp4xml/issues/250).
* Infinite loop when `<` was typed into an empty DTD file. See [#266](https://github.com/angelozerr/lsp4xml/issues/266).
* Formatting malformed xml removed content. See [#227](https://github.com/angelozerr/lsp4xml/issues/227).
* Misplace diagnostic for cvc-elt.3.1. See [#241](https://github.com/angelozerr/lsp4xml/issues/241).
* javax.xml.soap.Node is not available in Java 11. See [#238](https://github.com/angelozerr/lsp4xml/issues/238).
* Adjust range for DTD validation errors. See [#107](https://github.com/angelozerr/lsp4xml/issues/107).
* Adjust range error for internal DTD declaration. See [#225](https://github.com/angelozerr/lsp4xml/issues/225).
* Don't add sibling element when completion items is filled with grammar. See [#211](https://github.com/angelozerr/lsp4xml/issues/211).
* Validation needs additional `<uri>` catalog entry. See [#217](https://github.com/angelozerr/lsp4xml/issues/217).
* XML Schema completion prefix did not work in some cases. See [#214](https://github.com/angelozerr/lsp4xml/issues/214).
* Support rootUri for XML catalog configuration. See [#206](https://github.com/angelozerr/lsp4xml/issues/206).
* CacheResourcesManager keeps trying to download unavailable resources. See [#201](https://github.com/angelozerr/lsp4xml/issues/201).
* Support rootUri for XML catalog configuration. See [#206](https://github.com/angelozerr/lsp4xml/issues/206).
* CacheResourcesManager keeps trying to download unavailable resources. See [#201](https://github.com/angelozerr/lsp4xml/issues/201).
* Fix license headers according to project's declared EPL v2.0. See [#256](https://github.com/angelozerr/lsp4xml/issues/256).

## [0.0.2](https://github.com/angelozerr/lsp4xml/milestone/1?closed=1) (November 8, 2018)

### Enhancements

* Add support for `textDocument/documentLink` . See [#56](https://github.com/angelozerr/lsp4xml/issues/56).
* No completion nor validation when editing an xsd schema. See [#178](https://github.com/angelozerr/lsp4xml/issues/178).
* Cache on the file system, XML Schema from http, ftp before loading it. See [#159](https://github.com/angelozerr/lsp4xml/issues/159).
* Support for XSL. See [#189](https://github.com/angelozerr/lsp4xml/issues/189).
* Change 'resource downloading' diagnostic severity to Information. See [#187](https://github.com/angelozerr/lsp4xml/pull/187).
* XSL support to resolve XML Schema of xsl. See [#91](https://github.com/angelozerr/lsp4xml/issues/91).
* Add support for completion requests from empty character. See [#112](https://github.com/angelozerr/lsp4xml/issues/112).
* Provide documentation on hover for attributes. See [#146](https://github.com/angelozerr/lsp4xml/issues/146).

### Bug Fixes

* Formatting deletes document's body when there's a DTD declaration. See [#198](https://github.com/angelozerr/lsp4xml/issues/198).
* Completion from local xsd was cached too aggressively. See [#194](https://github.com/angelozerr/lsp4xml/issues/194).
* "format.splitAttributes:true" adds excessive indentation. See [#188](https://github.com/angelozerr/lsp4xml/issues/188).
* No validation or code completion on nested elements. See [#177](https://github.com/angelozerr/lsp4xml/issues/177).
* XSD files can only be edited if useCache is enabled. See [#186](https://github.com/angelozerr/lsp4xml/issues/186).
* No autocompletion when writing XSDs. See [#111](https://github.com/angelozerr/lsp4xml/issues/111).
* Insert required attribute code action inserts bad placeholder. See [#185](https://github.com/angelozerr/lsp4xml/issues/185).
* No validation when referencing a schema in the same directory. See [#144](https://github.com/angelozerr/lsp4xml/issues/144).
* Hover doesn't work when xs:annotation is declared in type and not element. See [#182](https://github.com/angelozerr/lsp4xml/issues/182).
* Incomplete autocompletion for xsl documents. See [#165](https://github.com/angelozerr/lsp4xml/issues/165).
* Auto Complete/ Completion for XML Prolog. See [#85](https://github.com/angelozerr/lsp4xml/issues/85).
* `xml.format.splitAttributes` keeps first attribute on same line. See [#161](https://github.com/angelozerr/lsp4xml/pull/161).
* File association should support relative path for systemId. See [#142](https://github.com/angelozerr/lsp4xml/issues/142).
* Validation of non-empty nodes required to be empty shows misplaced diagnostics. See [#147](https://github.com/angelozerr/lsp4xml/issues/147).
* Validation of empty required node shows misplaced diagnostics. See [#145](https://github.com/angelozerr/lsp4xml/issues/145).