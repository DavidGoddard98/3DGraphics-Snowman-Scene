public class AState {

/**
* This method takes a state as input and depending on its type, returns the
* a string after the relevant method has been enacted on it.
* If the state is not identified then the program stops.
* @param state This is the state given as input
* @return String The toString method of the state.
*/
String aProcess(AState state) {

  switch(state) {

    case state.equals(a):
       return state.doA().toString();
       break;
    case state.equals(b):
       return state.doB().toString();
       break;
    default:
      System.exit();
    }
}

}
