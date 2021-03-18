// import "./Welcome.css";
import Button from "@material-ui/core/Button";
import React from "react";
import { Link as RouterLink } from "react-router-dom";
import Grid from "@material-ui/core/Grid";

export default function Welcome() {
  return (
    <div>
      <Grid
        container
        spacing={0}
        direction="column"
        alignItems="center"
        justify="center"
        style={{ minHeight: "100vh" }}
      >
        <Grid item xs={3}>
          <header>
            <p>Welcome to our Wacc Compiler</p>
          </header>
        </Grid>
        <Grid item xs={3}>
          <Button
            variant="contained"
            color="primary"
            component={RouterLink}
            to="/compiler"
          >
            Write code
          </Button>
        </Grid>
      </Grid>
    </div>
  );
}
