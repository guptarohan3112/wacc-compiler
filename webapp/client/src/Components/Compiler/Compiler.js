import { makeStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
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

  const updateTextValue = (event) => {
    event.preventDefault();
    setText(event.target.value);
  };

  const updateTextValueFromFile = (value) => {
    setText(value);
  };

  const updateAssembly = (value) => {
    setAssembly(value);
  };

  const updateErrorMsg = (value) => {
    setErrorMsg(value);
  };

  const updateErrorCode = (value) => {
    setErrorCode(value);
    if (value == 0) {
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
    var file = event.target.files[0]
    console.log(file)

    if (file) {
      var reader = new FileReader();
      reader.readAsText(file, "UTF-8");
      reader.onload = function (evt) {
          console.log(evt.target.result)
          updateTextValueFromFile(evt.target.result);
      }
      reader.onerror = function (evt) {
          updateTextValueFromFile("error reading file");
      }
  }

  }

  const compile = async (event) => {
    const config = {
      method: "post",
      url: "http://localhost:8080/compile",
      data: text,
      headers: {
        "Content-Type": "text/plain",
      },
    };

    try {
      const response = await axios(config);
      updateAssembly(response.data.assembly);
      updateErrorCode(response.data.errorCode);
      updateErrorMsg(response.data.errorMsg);
      console.log(response.data);
    } catch (error) {
      console.warn(error);
    }
  };

  useEffect(() => {}, [text]);

  return (
    <div className={classes.root}>
      <div>
        <Grid
          container
          direction="row"
          justify="space-between"
          alignItems="center"
        >
          <header>
            <p>Welcome to our WACC Compiler</p>
          </header>
          <div>
            <input type="file" name="file" onChange={uploadFile} />
          </div>
          <Button variant="contained" color="primary" onClick={compile}>
            Compile
          </Button>
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
                rows={39}
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
                    rows={27}
                    variant="outlined"
                    InputProps={{
                      className: classes.input,
                    }}
                    disabled
                    value={assembly}
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
