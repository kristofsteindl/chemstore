import React, { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { logout, } from '../../utils/securityUtils'
import 'semantic-ui-css/semantic.min.css'
import Select from 'react-select'
import store from '../../store'
import { SELECT_LAB } from '../../actions/types'
import { ButtonGroup, Dropdown, DropdownButton } from 'react-bootstrap'


const Header = () => {
    //const [selectedLab, setSelectedLab] = useState("") 
    const security = useSelector((state) => state.security)
    const labs = useSelector((state) => state.labs)
    const selectedLab = useSelector((state) => state.selectedLab)

    const { user } = security;
    const userIsAuth = user && JSON.stringify(user) !== '{}'

    useEffect(() => {
        if (userIsAuth && labs.length > 0) {
            const storedLabKeyKey = `${user.username.split('@')[0]}.selectedLab`
            const storedLabKey = localStorage.getItem(storedLabKeyKey)
            if (storedLabKey) {
                const storedLab = labs.filter(lab => lab.key === storedLabKey)[0]
                handleLabSelection(storedLab)
            }
            else if (labs.length === 1) {
                handleLabSelection(labs[0])
            } 
        }
        
    }, [labs, security])

    const handleLogout = () => {
        logout()
        window.location.href = '/'
    }

    const handleLabSelection = justSelected => {
        localStorage.setItem(`${user.username.split('@')[0]}.selectedLab`, justSelected.key)
        store.dispatch({
            type: SELECT_LAB,
            payload: justSelected
        });
    }

    const userManual = (
        <li className="nav-item">
            <Link to='/user-manual' className="nav-link" >
                <i className="fas fa-question-circle"></i>
            </Link>

        </li>)
    
    const userISAuthenticated = (                    
        <div className="collapse navbar-collapse" id="mobile-nav">
            <ul className="navbar-nav mr-auto">
                <li className="nav-item">
                    <Link className="nav-link" to="/chem-items">
                        Chem items
                    </Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link " to="/users">
                        Users
                    </Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link " to="/labs">
                        Labs
                    </Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link " to="/manufacturers">
                        Manufacturers
                    </Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link" to="/categories">
                        Categories
                    </Link>
                </li>
                <li className="nav-item">
                    <Link className="nav-link " to="/chemicals">
                        Chemicals
                    </Link>
                </li>
            </ul>

            <ul className="navbar-nav ms-auto">
                <li className="nav-item" key="react-select">
                    <Select 
                        name="form-field-name"
                        value={selectedLab}
                        onChange={handleLabSelection}
                        options={labs} 
                        placeholder="Select lab"

                    />
                </li>
                <li className="nav-item">
                <ButtonGroup style={{"paddingLeft": "10px", "paddingRight": "10px"}}>
                    <DropdownButton as={ButtonGroup} title={<i style={{"color": "#dddddd"}} className="fas fa-user">{user.fullName}</i>} id="bg-nested-dropdown">
                        <Dropdown.Item eventKey="1" tag={Link} to='/change-password' style={{ textDecoration: 'none' }}>
                           
                                Change Password
                            
                        </Dropdown.Item>
                        <Dropdown.Item eventKey="2" tag={Link} to="/" onClick={handleLogout} style={{ textDecoration: 'none' }}>
                            
                                Logout
                            
                        </Dropdown.Item>
                    </DropdownButton>
                </ButtonGroup>

                </li>
                {userManual}
            </ul>
        </div>
    )
    const userIsNOTAuthenticated = (
        <div className="collapse navbar-collapse" id="mobile-nav">
            <ul className="navbar-nav ms-auto">
                <li className="nav-item">
                    <Link className="nav-link" to='/login'>
                        Login
                    </Link>
                </li>
                {userManual}
            </ul>
        </div>
    )

    let headerLinks = userIsAuth ? userISAuthenticated : userIsNOTAuthenticated;

    return (
        <nav className="navbar navbar-expand-sm navbar-dark bg-primary mb-4">
            <div className="container">
                <Link className="navbar-brand" to="/">
                    chemstore
                </Link>
                {
                    headerLinks
                }
            </div>
        </nav>
    )
    
}

export default Header
