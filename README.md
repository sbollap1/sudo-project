sudo-project
============
This version is a better version. Because I am doing the following:

1. Creating a Thread to do the recursion in the background and 
2. Use a Push solution

However the response time doesn't seem to be very fast. Seems there is something wrong with the application as 
even after the puzzle is solved there is this red spinner on the top right that is still spinning. Seems like the
thread is not stopped gracefully.

I have tested with 2 input files one which will not go deep into the recursion, and another which goes a little bit 
deeper. The one which goes little bit deeper is the one seems to be not returning. But the puzzle (even the hard puzzle) 
is solved and table is updated nevertheless.

I don't think I need to have a return at the end of run() method. But, I have it there to see if that helps. But doesn't
look like it.

Any suggestions welcome please and thank you in advance.
