#ifndef _H_APPSERVER
#define _H_APPSERVER

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
//  AppServer.h
//
// Description
//  Encapsulates a TcpServer object.
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

#include "TcpServer.h"
#include "TcpClient.h"
#include "TcpConnectionListener.h"

// Foward declarations.
class AppClient;

class AppServer :
    /* implements */ public TcpConnectionListener
{
// Member functions.
public:

    // Default constructor.
    AppServer();

    ~AppServer();

    // Create a TCP server object and open it.
    void open(unsigned short port);

    // Close the TCP service.
    void close();

    // Accepted client socket closed.
    void clientClosed(const AppClient& client);

    // TCP connection listener callback methods.
    void opened(TcpConnection& connection);
    void openFailed(const char *reason,
                    TcpConnection& connection);
    void halfClosed(TcpConnection& connection) {};
    void closed(const char *reason,
                TcpConnection& connection);
    void accepted(TcpClient& client, TcpServer& server);

    // These callbacks are never generated by a TCP server.
    void transmitted(TcpConnection&) {};
    void transmitFailed(const char*, TcpConnection&) {};
    void receive(const char*, int, TcpConnection&) {};

protected:
private:

// Nested classes.
public:
protected:
private:

    class ClientEntry
    {
    public:
        ClientEntry(AppClient& client)
        : _client(&client),
          _next(NULL)
        {};

        ~ClientEntry() {};

        AppClient* getClient() const
        { return(_client);};

        ClientEntry* getNext() const
        { return(_next);};

        void setNext(ClientEntry *entry)
        { _next = entry; };

    private:
        AppClient *_client;
        ClientEntry *_next;

        friend class AppServer;
    };

// Member data.
public:
protected:
private:

    // The TCP service itself.
    TcpServer *_server;

    // Keep track of all the accepted client connections.
    // When this application terminates, they will all be
    // closed and deleted.
    ClientEntry *_clientList;
};

#endif
