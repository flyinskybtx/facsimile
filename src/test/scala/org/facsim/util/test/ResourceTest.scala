/*
Facsimile -- A Discrete-Event Simulation Library
Copyright © 2004-2012, Michael J Allen.

This file is part of Facsimile.

Facsimile is free software: you can redistribute it and/or modify it under the
terms of the GNU Lesser General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option) any
later version.

Facsimile is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
details.

You should have received a copy of the GNU Lesser General Public License along
with Facsimile.  If not, see http://www.gnu.org/licenses/lgpl.

The developers welcome all comments, suggestions and offers of assistance.  For
further information, please visit the project home page at:

  http://facsim.org/

Thank you for your interest in the Facsimile project!

IMPORTANT NOTE: All patches (modifications to existing files and/or the
addition of new files) submitted for inclusion as part of the official
Facsimile code base, must comply with the published Facsimile Coding Standards.
If your code fails to comply with the standard, then your patches will be
rejected.  For further information, please visit the coding standards at:

  http://facsim.org/Documentation/CodingStandards/
===============================================================================
Scala source file from the org.facsim.util.test package.
*/
//=============================================================================

package org.facsim.util.test

import java.util.GregorianCalendar
import java.util.Locale
import java.util.MissingResourceException
import org.facsim.util.Resource
import org.scalatest.FunSpec
import scala.Array.apply

//=============================================================================
/**
Test harness for the [[org.facsim.util.Resource!]] class.
*/
//=============================================================================

class ResourceTest extends FunSpec {

/*
Some commonly used strings.
*/

  trait testResources {
    val compoundResource = Array ("testCompoundResource0",
    "testCompoundResource1", "testCompoundResource2")
    val dateResource = "testDateResource"
    val helloResource = "hello"
    val realResource = "testRealResource"
    val singleResource = "testSingleResource"
    val testBundleName = "facsimiletest"
  }

/*
Name the class we're testing.
*/

  describe (classOf [Resource].getCanonicalName) {

/*
Test primary constructor operation.
*/

    describe ("this (String)") {
      it ("must throw NullPointerException when bundleName is null") {
        val e = intercept [NullPointerException] {
          new Resource (null)
        }
        assert (e.getMessage () === "Argument 'bundleName' cannot be null.")
      }
      it ("must throw MissingResourceException when bundleName identifies a " +
      "missing resource bundle") {
        intercept [MissingResourceException] {
          new Resource ("NameOfNonExistentBundle")
        }
      }
      it ("must find a defined resource bundle OK") {
        new testResources {
          assert (new Resource (testBundleName) ne null)
        }
      }
    }

/*
Test string resource formatting.
*/

    describe ("format (String, Any*)") {
      it ("must throw NullPointerException when key is null") {
        new testResources {
          val e = intercept [NullPointerException] {
            new Resource (testBundleName).format (null)
          }
          assert (e.getMessage () === "Argument 'key' cannot be null.")
        }
      }
      it ("must throw MissingResourceException when key undefined") {
        new testResources {
          intercept [MissingResourceException] {
            new Resource (testBundleName).format ("UNDEFINED_KEY")
          }
        }
      }
      ignore ("must throw ClassCastException when key identifies non-string " +
      "resource") {
        new testResources {
          // Cannot define non-string resources currently - test ignored
          intercept [ClassCastException] {
            new Resource (testBundleName).format ("testNonStringResource")
          }
        }
      }
      ignore ("must throw IllegalArgumentException when argument passed to " +
      "non-compound resource") {
        new testResources {
          // Java doesn't throw an exception in this case - test ignored.
          intercept [IllegalArgumentException] {
            new Resource (testBundleName).format (singleResource,
            "Invalid extra argument")
          }
        }
      }
      ignore ("must throw IllegalArgumentException when no argument passed to "
      + "compound resource") {
        new testResources {
          // Java doesn't throw an exception in this case - test ignored.
          intercept [IllegalArgumentException] {
            new Resource (testBundleName).format (compoundResource (0))
          }
        }
      }
      ignore ("must throw IllegalArgumentException when extra arguments " +
      "passed to compound resource") {
        new testResources {
          // Java doesn't throw an exception in this case - test ignored.
          intercept [IllegalArgumentException] {
            new Resource (testBundleName).format (compoundResource (0),
            "Valid argument", "Invalid extra argument")
          }
        }
      }
      ignore ("must throw IllegalArgumentException when insufficient " +
      "arguments passed to compound resource") {
        new testResources {
          // Java doesn't throw an exception in this case - test ignored.
          intercept [IllegalArgumentException] {
            new Resource (testBundleName).format (compoundResource (1),
            "Valid argument")
          }
        }
      }
      it ("must throw IllegalArgumentException when string passed to date " +
      "resource") {
        new testResources {
          intercept [IllegalArgumentException] {
            new Resource (testBundleName).format (dateResource, "Bob")
          }
        }
      }
      it ("must throw IllegalArgumentException when string passed to real " +
      "resource") {
        new testResources {
          intercept [IllegalArgumentException] {
            new Resource (testBundleName).format (realResource, "Fred")
          }
        }
      }
      it ("must retrieve a non-compound resource with no arguments OK") {
        new testResources {
          assert (new Resource (testBundleName).format (singleResource) ===
          "Test non-compound resource")
        }
      }
      it ("must retrieve compound resources with appropriate arguments OK") {
        new testResources {
          val resource = new Resource (testBundleName)
          assert (resource.format (compoundResource (0), "zero") ===
          "Test compound resource 0: 0=zero")
          assert (resource.format (compoundResource (1), "zero", "one") ===
          "Test compound resource 1: 0=zero, 1=one")
          assert (resource.format (compoundResource (2), "zero", "one", "two")
          === "Test compound resource 2: 0=zero, 1=one, 2=two")
        }
      }
      it ("must parse choice resources OK") {
        new testResources {
          val choiceResource = "testChoiceResource"
          assert (new Resource (testBundleName).format (choiceResource, 0) ===
          "On your marks...")
          assert (new Resource (testBundleName).format (choiceResource, 1) ===
          "Get set...")
          assert (new Resource (testBundleName).format (choiceResource, 2) ===
          "Go!")
        }
      }
      it ("must retrieve localized string resources OK") {
        new testResources {
          val default = Locale.getDefault ()
          try {
            // Check that we get the correct en_US response.
            Locale.setDefault (Locale.US)
            assert (new Resource (testBundleName).format (helloResource) ===
            "Howdy!")
            // Should get a different result for Brits...
            Locale.setDefault (Locale.UK)
            assert (new Resource (testBundleName).format (helloResource) ===
            "Wotcha!")
            // Germans should have a good day...
            Locale.setDefault (Locale.GERMANY)
            assert (new Resource (testBundleName).format (helloResource) ===
            "Guten Tag!")
            // ...and so should the French...
            Locale.setDefault (Locale.FRANCE)
            assert (new Resource (testBundleName).format (helloResource) ===
            "Bonjour!")
          }
          // Restore original default.
          finally {
            Locale.setDefault (default)
          }
        }
      }
      it ("must retrieve numeric resources OK") {
        new testResources {
          val default = Locale.getDefault ()
          val number = 1234.56
          try {
            // Check that we get the correct en_US response.
            Locale.setDefault (Locale.US)
            assert (new Resource (testBundleName).format (realResource, number)
            === "1,234.56")
            // Germans do things differently...
            Locale.setDefault (Locale.GERMANY)
            assert (new Resource (testBundleName).format (realResource, number)
            === "1.234,56")
          }
          // Restore original default.
          finally {
            Locale.setDefault (default)
          }
        }
      }
      it ("must retrieve localized date resources OK") {
        new testResources {
          val default = Locale.getDefault ()
          // Oct 14, 2010 - months are zero-based numbers (!)
          val date = new GregorianCalendar (2010, 9, 14).getTime ()
          try {
            // Check that we get the correct en_US response.
            Locale.setDefault (Locale.US)
            assert (new Resource (testBundleName).format (dateResource, date)
            === "10/14/10")
            // Month & day are in different order in the UK
            Locale.setDefault (Locale.UK)
            assert (new Resource (testBundleName).format (dateResource, date)
            === "14/10/10")
            // Germans use same order as UK, but different separator...
            Locale.setDefault (Locale.GERMANY)
            assert (new Resource (testBundleName).format (dateResource, date)
            === "14.10.10")
          }
          // Restore original default.
          finally {
            Locale.setDefault (default)
          }
        }
      }
    }
  }
}