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
// Name
//  TcpConnectionListener.java
//
// Description
//  Callback interface for TCP connections.
//
// RCS ID
// $Id$
//
// CHANGE LOG
// $Log$
// Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
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
// Revision 1.1  2000/10/16 20:58:44  charlesr
// Initial version.
//

/* package */ interface TcpConnectionListener
{
    public void opened(TcpConnection client);
    public void openFailed(String reason, TcpConnection client);
    public void halfClosed(TcpConnection client);
    public void closed(String reason, TcpConnection client);
    public void transmitted(TcpConnection client);
    public void transmitFailed(String reason, TcpConnection client);
    public void receive(byte[] data, TcpConnection client);
    public void accepted(TcpClient client, TcpServer server);
}
