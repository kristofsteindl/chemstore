import { Button } from "@mui/material"
import { Link } from "react-router-dom"
import { check } from "../../utils/securityUtils"
import "./DuButtons.css"

const DuButtons = props => {

    const checkAndUpdate = () => {
        props.onDelete()
    }

    const checkAndDelete = () => {
        check()
        props.onDelete()
    }
    return (
        <div id="button-group">
            <Link className={`${props.updateDisabled && "disabled-link"}`} to={props.updateFormTo} disabled={true} onClick={check}>
                <Button className="btn" variant="outlined" size="small" disabled={props.updateDisabled}>
                    <i className="fa fa-edit">Update</i>
                </Button>
            </Link>
            
            <Button className="btn" color="error" variant="outlined" size="small" onClick={checkAndDelete}  >
                <i className="fa fa-minus-circle">Delete</i>
            </Button>
        </div>
    )
}

export default DuButtons