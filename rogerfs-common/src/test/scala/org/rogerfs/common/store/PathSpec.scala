package org.rogerfs.common.store

import org.scalatest.WordSpec

class PathSpec extends WordSpec {

  "A path" when {
    "empty" should {
      val path = ""
      "be invalid" in {
        assert(!Path.isValid(path))
      }
      "produce InvalidPathException when getPath is invoked" in {
        intercept[InvalidPathException] {
          Path.getPath(path)
        }
      }
    }

    "a slash path" should {
      val path = "/"
      "be invalid" in {
        assert(!Path.isValid(path))
      }
      "produce InvalidPathException when getPath is invoked" in {
        intercept[InvalidPathException] {
          Path.getPath(path)
        }
      }
    }

    "a path with invalid char '&'" should {
      val path = "/abc/de&f"
      "be invalid" in {
        assert(!Path.isValid(path))
      }
      "produce InvalidPathException when getPath is invoked" in {
        intercept[InvalidPathException] {
          Path.getPath(path)
        }
      }
    }
    "a path with invalid char '\\'" should {
      val path = "/abc/de\\f"
      "be invalid" in {
        assert(!Path.isValid(path))
      }
      "produce InvalidPathException when getPath is invoked" in {
        intercept[InvalidPathException] {
          Path.getPath(path)
        }
      }
    }

    "a root directory" should {
      val path = "/abc"
      "be valid" in {
        assert(Path.isValid(path))
      }
      val pathObject = Path.getPath(path)
      "parent is '/'" in {
        assert(pathObject.getParent == "/")
      }
      "name is 'abc'" in {
        assert(pathObject.getName == "abc")
      }
      "path is '/abc'" in {
        assert(pathObject.getPath == "/abc")
      }
    }

    "a root directory with slash" should {
      val path = "/abc/"
      "be valid" in {
        assert(Path.isValid(path))
      }
      val pathObject = Path.getPath(path)
      "parent is '/'" in {
        assert(pathObject.getParent == "/")
      }
      "name is 'abc'" in {
        assert(pathObject.getName == "abc")
      }
      "path is '/abc'" in {
        assert(pathObject.getPath == "/abc")
      }

    }

    "second level directory" should {
      val path = "/abc/cde"
      "be valid" in {
        assert(Path.isValid(path))
      }

      val pathObject = Path.getPath(path)
      "parent is '/abc'" in {
        assert(pathObject.getParent == "/abc")
      }
      "name is 'cde'" in {
        assert(pathObject.getName == "cde")
      }
      "path is '/abc/cde'" in {
        assert(pathObject.getPath == "/abc/cde")
      }

    }

    "second level directory with slash" should {
      val path = "/abc/cde/"
      "be valid" in {
        assert(Path.isValid(path))
      }

      val pathObject = Path.getPath(path)
      "parent is '/abc'" in {
        assert(pathObject.getParent == "/abc")
      }
      "name is 'cde'" in {
        assert(pathObject.getName == "cde")
      }
      "path is '/abc/cde'" in {
        assert(pathObject.getPath == "/abc/cde")
      }

    }

    "third level directory" should {
      val path = "/abc/cde/fgh"
      "be valid" in {
        assert(Path.isValid(path))
      }

      val pathObject = Path.getPath(path)
      "parent is '/abc/cde'" in {
        assert(pathObject.getParent == "/abc/cde")
      }
      "name is 'fgh'" in {
        assert(pathObject.getName == "fgh")
      }
      "path is '/abc/cde/fgh'" in {
        assert(pathObject.getPath == "/abc/cde/fgh")
      }

    }

    "third level directory with slash" should {
      val path = "/abc/cde/fgh/"
      "be valid" in {
        assert(Path.isValid(path))
      }

      val pathObject = Path.getPath(path)
      "parent is '/abc/cde'" in {
        assert(pathObject.getParent == "/abc/cde")
      }
      "name is 'fgh'" in {
        assert(pathObject.getName == "fgh")
      }
      "path is '/abc/cde/fgh'" in {
        assert(pathObject.getPath == "/abc/cde/fgh")
      }

    }
    "third level file" should {
      val path = "/abc/cde/fgh.ext"
      "be valid" in {
        assert(Path.isValid(path))
      }
      val pathObject = Path.getPath(path)
      "parent is '/abc/cde'" in {
        assert(pathObject.getParent == "/abc/cde")
      }
      "name is 'fgh.ext'" in {
        assert(pathObject.getName == "fgh.ext")
      }
      "path is '/abc/cde/fgh.ext'" in {
        assert(pathObject.getPath == "/abc/cde/fgh.ext")
      }

    }

  }
}
