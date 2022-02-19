import { Button } from "@mui/material"
import Modal from "./Modal"
import "./VerifyPanel.css"

const VerifyPanel = props => {
    return (
        <Modal onClick={props.onCancel}>
            <div className="message">
                {props.veryfyMessage} 
            </div>
            <div className="button-container">
                <Button style={{marginRight: "10px"}}variant="contained" onClick={props.onCancel}>Cancel</Button>
                <Button  variant="contained" onClick={props.onSubmit}>{props.buttonLabel}</Button>
            </div>

        </Modal>
    )
}

export default VerifyPanel