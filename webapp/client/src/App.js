import React, { Component } from "react";
import Welcome from "./Components/Welcome/Welcome";
import Compiler from "./Components/Compiler/Compiler";
import { Route, BrowserRouter as Router } from "react-router-dom";

class App extends Component {
  render() {
    return (
        <Router>
          <Route exact path="/" component={Welcome}/>
          <Route exact path="/compiler" component={Compiler} />
        </Router>
    );
  }
}

export default App;
