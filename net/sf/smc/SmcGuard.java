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
// Revision 1.1  2001/12/03 14:14:03  cwrapp
// Changes in release 1.0.2:
// + Placed the class files in Smc.jar in the net.sf.smc package.
// + Moved Java source files from smc/bin to net/sf/smc.
// + Corrected a C++ generation bug wherein arguments were written
//   to the .h file rather than the .cpp file.
//
// Revision 1.1.1.2  2001/03/26 14:41:46  cwrapp
// Corrected Entry/Exit action semantics. Exit actions are now
// executed only by simple transitions and pop transitions.
// Entry actions are executed by simple transitions and push
// transitions. Loopback transitions do not execute either Exit
// actions or entry actions. See SMC Programmer's manual for
// more information.
//
// Revision 1.1.1.1  2001/01/03 03:13:59  cwrapp
//
// ----------------------------------------------------------------------
// SMC - The State Map Compiler
// Version: 1.0, Beta 3
//
// SMC compiles state map descriptions into a target object oriented
// language. Currently supported languages are: C++, Java and [incr Tcl].
// SMC finite state machines have such features as:
// + Entry/Exit actions for states.
// + Transition guards
// + Transition arguments
// + Push and Pop transitions.
// + Default transitions. 
// ----------------------------------------------------------------------
//
// Revision 1.2  2000/09/01 15:32:07  charlesr
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

package net.sf.smc;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.ListIterator;

public abstract class SmcGuard
{
// Member Methods

    public SmcGuard(SmcAction condition, int line_number)
    {
        _condition = condition;
        _line_number = line_number;
        _end_state = "";
        _actions = new LinkedList();
    }

    public SmcAction getCondition()
    {
        return(_condition);
    }

    public int getLineNumber()
    {
        return(_line_number);
    }

    public int getTransType()
    {
        return(_trans_type);
    }

    public void setTransType(int trans_type)
    {
        _trans_type = trans_type;
        return;
    }

    public String getEndState()
    {
        return(_end_state);
    }

    public void setEndState(String end_state)
    {
        _end_state = end_state;
        return;
    }

    public void appendEndState(String token)
    {
        _end_state += token;
        return;
    }

    public void addAction(SmcAction action)
    {
        _actions.add(action);
        return;
    }

    public String toString()
    {
        String retval;
        String separator;
        ListIterator action_it;
        SmcAction action;

        if (_condition == null)
        {
            retval = "";
        }
        else
        {
            retval = "[" + _condition + "]";
        }

        switch(_trans_type)
        {
            case Smc.TRANS_NOT_SET:
                retval += " not set";
                break;

            case Smc.TRANS_SET:
                retval += " set";
                break;

            case Smc.TRANS_PUSH:
                retval += " push";
                break;

            case Smc.TRANS_POP:
                retval += " pop";
                break;
        }

        retval += " " + _end_state;

        retval += " {";
        for (action_it = _actions.listIterator(),
                 separator = "";
             action_it.hasNext() == true;
             separator = " ")
        {
            action = (SmcAction) action_it.next();
            retval += separator + action;
        }
        retval += "}";

        return(retval);
    }

    public abstract void generateCode(PrintStream source,
                                      int guardIndex,
                                      int guardCount,
                                      String context,
                                      String mapName,
                                      String stateName,
                                      String indent)
        throws ParseException;

// Member Data

    protected SmcAction _condition;
    protected int _line_number;
    protected int _trans_type;
    protected String _end_state;
    protected LinkedList _actions;
}
