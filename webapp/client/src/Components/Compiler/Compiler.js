import { makeStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import Switch from "@material-ui/core/Switch";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import React, { useState, useEffect } from "react";
import axios from "axios";

const useStyles = makeStyles((theme) => ({
  root: {
    // "&:hover .MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
    //   borderColor: "red"
    // },
    // "& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline": {
    //   borderColor: "purple"
    // },
    padding: "10px",
    spacing: theme.spacing(5),
    "& .MuiFormControlLabel-labelPlacementBottom": {
      color: theme.palette.text.primary,
      padding: "3px",
    },
    // maxWidth: "500px"
    // flexGrow: 2
  },

  textbox: {
    textAlign: "center",
    width: "100%",
    height: "100%",
    color: theme.palette.text.primary,
    padding: theme.spacing(1),
  },
  input: {
    height: "100%",
  },
  error: {
    textAlign: "center",
    width: "100%",
    height: "100%",
    color: theme.palette.text.primary,
    padding: theme.spacing(1),
    // "& .MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
    //   borderColor: "grey"
    // },
  },
}));

export default function Compiler() {
  const classes = useStyles();

  const [text, setText] = useState("");
  const [assembly, setAssembly] = useState("");
  const [errorCode, setErrorCode] = useState(0);
  const [errorMsg, setErrorMsg] = useState("");
  const [optimise, setOptimise] = useState(false);
  const [output, setOutput] = useState("");
  const [display, setDisplay] = useState("");

  const switchOptimise = (event) => {
    console.log(assembly)
    setOptimise(event.target.checked);
  };

  const updateTextValue = (event) => {
    event.preventDefault();
    setText(event.target.value);
  };

  const updateAssembly = (value) => {
    setAssembly(value);
  };

  const updateOutput = (value) => {
    setOutput(value);
  };

  const updateErrorMsg = (value) => {
    setErrorMsg(value);
  };

  const updateErrorCode = (value) => {
    setErrorCode(value);
    if (value === 0) {
      document
        .getElementsByClassName("MuiOutlinedInput-notchedOutline")
        .item(2).style.borderColor = "red";
    } else {
      document
        .getElementsByClassName("MuiOutlinedInput-notchedOutline")
        .item(2).style.borderColor = "green";
    }
  };

  const uploadFile = (event) => {
    var file = event.target.files[0];
    console.log(file);

    if (file) {
      var reader = new FileReader();
      reader.readAsText(file, "UTF-8");
      reader.onload = function (evt) {
        console.log(evt.target.result);
        setText(evt.target.result);
      };
      reader.onerror = function (evt) {
        setText("error reading file");
      };
    }
  };

  const compile = async (event) => {
    runCode("c")
  };

  const execute = async (event) => {
    runCode("e")
  };

  const runCode = async (option) => {
    const config = {
      method: "post",
      url: "http://localhost:8080/compile",
      data: {
        code: text,
        optimise: optimise ? 1 : 0,
      },
    };

    try {
      const response = await axios(config);
      updateAssembly(response.data.assembly);
      updateOutput(response.data.output);
      updateErrorCode(response.data.errorCode);
      updateErrorMsg(response.data.errorMsg);
      if (option === "c") {
        setDisplay(response.data.assembly)
      } else {
        if (response.data.output === ""){
          setDisplay("No output produced.")
        }
        else{
          setDisplay(response.data.output)
        }
      }
      console.log("request done");
    } catch (error) {
      console.warn(error);
    }
  };

  useEffect(() => {}, [text]);
  // useEffect(() => {}, [assembly]);
  // useEffect(() => {}, [output]);
  // useEffect(() => {}, [display]);

  return (
    <div className={classes.root}>
      <div>
        <Grid
          container
          direction="row"
          justify="space-between"
          alignItems="center"
        >
          <Grid item xs={1}>
            <header>
              <p>Welcome to our WACC Compiler</p>
            </header>
          </Grid>
          <Grid item xs={1}>
            <div>
              <input type="file" name="file" onChange={uploadFile} />
            </div>
          </Grid>

          <Grid item xs={3}>
            <Grid
              container
              direction="row"
              justify="space-evenly"
              // justify= "space-"
              alignItems="center"
            >
              <Grid item xs={1}>
                <FormControlLabel
                  value="optimise"
                  control={
                    <Switch
                      checked={optimise}
                      onChange={switchOptimise}
                      name="optimise"
                      color="primary"
                    />
                  }
                  label="Optimise"
                  labelPlacement="bottom"
                />
              </Grid>
              <Grid item xs={1}>
                <Button variant="contained" color="primary" onClick={compile}>
                  Compile
                </Button>
              </Grid>
              <Grid item xs={1}>
                <Button variant="contained" color="primary" onClick={execute}>
                  Execute
                </Button>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </div>

      <div>
        <Grid
          container
          direction="row"
          justify="space-between"
          alignItems="flex-start"
        >
          <Grid item xs={7}>
            <div>
              <TextField
                id="outlined-textarea"
                className={classes.textbox}
                label="Code"
                multiline
                rows={38}
                variant="outlined"
                InputProps={{
                  className: classes.input,
                }}
                value={text}
                onChange={updateTextValue}
              />
            </div>
          </Grid>

          <Grid item xs={5}>
            <Grid container direction="column">
              <Grid item xs>
                <div>
                  <TextField
                    id="outlined-textarea"
                    className={classes.textbox}
                    label="Click compile button to view assembly..."
                    multiline
                    rows={26}
                    variant="outlined"
                    InputProps={{
                      className: classes.input,
                    }}
                    disabled
                    value={display}
                  />
                </div>
              </Grid>

              <Grid item xs>
                <div>
                  <TextField
                    id="outlined-textarea"
                    className={classes.error}
                    multiline
                    rows={9}
                    variant="outlined"
                    InputProps={{
                      className: classes.input,
                    }}
                    disabled
                    // onChange={updateColour}
                    value={errorMsg}
                  />
                </div>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </div>
    </div>
  );
}
