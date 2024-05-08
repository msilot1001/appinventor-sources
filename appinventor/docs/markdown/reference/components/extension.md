---
layout: documentation
title: Extension
---

[&laquo; Back to index](index.html)
# Extension

Table of Contents:

* [SocketIO](#SocketIO)

## SocketIO  {#SocketIO}

Component for SocketIO



### Properties  {#SocketIO-Properties}

{:.properties}

{:id="SocketIO.RandomizationFactor" .number} *RandomizationFactor*
: Specifies the randomization factor used when reconnecting (so that the clients do not reconnect at the exact same time after a server crash, for example).

{:id="SocketIO.Reconnection" .boolean} *Reconnection*
: Specifies whether reconnection is enabled or not

{:id="SocketIO.ReconnectionAttempts" .number} *ReconnectionAttempts*
: Specifies the number of reconnection attempts before giving up.

{:id="SocketIO.ReconnectionDelay" .number} *ReconnectionDelay*
: Specifies the initial delay before reconnection in milliseconds (affected by the randomizationFactor value).

{:id="SocketIO.ReconnectionDelayMax" .number} *ReconnectionDelayMax*
: Specifies the maximum delay between two reconnection attempts. Each attempt increases the reconnection delay by 2x.

{:id="SocketIO.ServerIP" .text .ro} *ServerIP*
: The IP of the server connecting to

{:id="SocketIO.Timeout" .number} *Timeout*
: Specifies the timeout in milliseconds for each connection attempt.

### Events  {#SocketIO-Events}

{:.events}
None


### Methods  {#SocketIO-Methods}

{:.methods}

{:id="SocketIO.CreateSocket" class="method"} <i/> CreateSocket()
: Creates a Socket to the server with the provided IP
