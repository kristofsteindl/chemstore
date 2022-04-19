import { Button } from "@mui/material"
import Modal from "./Modal"
import "./VerifyPanel.css"

const VerifyPanel = props => {
    console.log("Inside VerifyPanel")
    return (
        <Modal onClick={props.onCancel}>
            <div className="message">
                {props.veryfyMessage} 
            </div>
            <div className="button-container">
                <Button style={{marginRight: "10px"}} variant="contained" onClick={props.onCancel}>{props.onSubmit ? "Cancel" : props.buttonLabel}</Button>
                {props.onSubmit &&
                    <Button  variant="contained" onClick={props.onSubmit}>{props.buttonLabel}</Button>
                }
            </div>

        </Modal>
    )
}

export default VerifyPanel