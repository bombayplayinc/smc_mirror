//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// The Original Code is State Map Compiler (SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s): 
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1  2001/01/03 03:14:00  cwrapp
// Initial revision
//
// Revision 1.2  2000/09/01 15:32:16  charlesr
// Changes for v. 1.0, Beta 2:
//
// + Removed order dependency on "%start", "%class" and "%header"
//   appearance. These three tokens may now appear in any order but
//   still must appear before the first map definition.
//
// + Modified SMC parser so that it will continue after finding an
//   error. Also improved the error message quality.
//
// + Made error messages so emacs is able to parse them.
//
// Revision 1.1.1.1  2000/08/02 12:50:56  charlesr
// Initial source import, SMC v. 1.0, Beta 1.
//

import java.io.PrintStream;
import java.text.ParseException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

public final class SmcParseTreeTcl
    extends SmcParseTree
{
    public SmcParseTreeTcl()
    {
        super();
    }

    public void generateCode(PrintStream header,
                             PrintStream source,
                             String srcfileBase)
        throws ParseException
    {
        ListIterator mapIt;
        ListIterator transIt;
        ListIterator paramIt;
        SmcMap map;
        SmcTransition trans;
        SmcParameter parameter;
        String separator;

        // Now dump out the raw source code, if any.
        if (_source != null && _source.length() > 0)
        {
            source.println(_source + "\n");
        }

        // Generate the context.
        source.println("class " +
                       _context +
                       "Context {");
        source.println("    inherit ::statemap::FSMContext;\n");
        source.println("# Member functions.\n");
        source.println("    constructor {owner} {");
        source.println("        ::statemap::FSMContext::constructor;");
        source.println("    } {");
        source.println("        set _owner $owner;");
        source.println("        setState ${" +
                       _start_state +
                       "};");

        // If transition queuing is being done, then initialize
        // the queue here.
        if (Smc.isTransQueue() == true)
        {
            source.println("        set _trans_queue {};");
        }

        source.println("    }\n");
        source.println("    public method getOwner {} {");
        source.println("        return -code ok $_owner;");
        source.println("    }");

        // For every possible transition in every state map,
        // create a method.
        // First, get the transitions list.
        LinkedList transList = new LinkedList();
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();

            // Merge the new transitions into the current set.
            transList =
                    Smc.merge(map.getTransitions(),
                              transList,
                              new Comparator() {
                                   public int compare(Object o1,
                                                      Object o2) {
                                       return(((SmcTransition) o1).compareTo((SmcTransition) o2));
                                   }
                               });
        }

        for (transIt = transList.listIterator();
             transIt.hasNext() == true;
            )
        {
            trans = (SmcTransition) transIt.next();
            source.print("\n    public method " +
                         trans.getName() +
                         " {");
            for (paramIt = trans.getParameters().listIterator(),
                         separator = "";
                 paramIt.hasNext() == true;
                 separator = " ")
            {
                parameter = (SmcParameter) paramIt.next();
                source.print(separator);
                parameter.generateCode(source);
            }
            source.println("} {");

            // If transition queuing, then queue the transition
            // and its arguments and execute them in from the
            // dispatch transitions method.
            if (Smc.isTransQueue() == true)
            {
                source.print("        lappend _trans_queue [list " +
                             trans.getName() +
                             " [list");
                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(" $" + parameter.getName());
                }
                source.println("];\n");
                source.println("        if {[string compare $_state \"\"] == 0} {");
                source.println("            dispatchTransitions;");
                source.println("        }");
            }
            else
            {
                source.print("        [getState] " +
                             trans.getName() +
                             " $this");
                for (paramIt = trans.getParameters().listIterator();
                     paramIt.hasNext() == true;
                    )
                {
                    parameter = (SmcParameter) paramIt.next();
                    source.print(" $" + parameter.getName());
                }
                source.println(";");
                source.println("        return -code ok;");
                source.println("    }");
            }
        }

        if (Smc.isTransQueue() == true)
        {
            source.println("\n    private method dispatchTransitions {} {");
            source.println("        while {[llength $_trans_queue] > 0} {");
            source.println("            set transition [lindex $_trans_queue 0];");
            source.println("            set _trans_queue [lreplace $_trans_queue 0 0];");
            source.println("            eval $_state [lindex $transition 0] [lindex $transition 1];");
            source.println("        }");
            source.println("    }");
        }

        source.println("\n# Member data.\n");
        source.println("    private variable _owner;");

        // If transition queuing, declare the necessary data.
        if (Smc.isTransQueue() == true)
        {
            source.println("    private variable _trans_queue;");
        }

        // Put the closing brace on the context class.
        source.println("}\n");

        // Have each map print out its source code in turn.
        for (mapIt = _maps.listIterator();
             mapIt.hasNext() == true;
            )
        {
            map = (SmcMap) mapIt.next();
            map.generateCode(header, source, _context);
        }

        return;
    }
}
