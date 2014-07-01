mcf-filesystem-xml-connector
============================

manifold connector for xml documents on filesystem
The code is currently limited to solrxml format but it could be generalized.
I used mcf-filesystem-connector as a starting point and borrowed carrydown data functionality from the rss connector.

---Purpose---
My goals writing this code are learning Manifold and contributing to the project.
The code is not currently usable for production
(it's not well tested, and generally no effort has been spent to make it robust)


--Proposals--

-Simplify UI development-
I find the UI code of mcf-filesystem-connector complex and unpleasant to write. 
My idea to simplify things is to write reusable widgets
I wrote the FileSelector widgets which mimics the functionality of fileselection of the starting code.
For an example of its usage see outputSpecificationBody.vtl
The widget write its data as json on a single field and is paired with a domain java class 
(FileSelectorCriteria) that also performs parsing.

I know that writing widget like this isn't exactly simple for backend developer but I think that a small 
widget library could cover the most common requirements and let backend developer work on backend code.
When a new widget is needed I think a UI developer would feel much more at home with the proposed approach.
The widget was developed using only static resources (see testpage.html) and then a velocity template was created
from it.

-Modularize code-
Much of the original code was crammed in a single class. I find it very hard to navigate the code this way 
and even harder to test it. I proposed a modularization for this connector, it would need more refactoring
but I hope it conveys the intention

-Use domain Object-
I think the domain object FileSelectorCriteria simplifies a lot the code that deals with file selection, 
compared to working directly with DocumentSpecification.


--Questions--

-How to do the file parsing only once
currently I'm using carrydown values but I'm not convinced this is the best way.
See comment in 
CrawlingServiceImpl.ingestFile

-Versioning for solrdocuments
See comment in
CrawlingServiceImpl.getDocumentVersions






