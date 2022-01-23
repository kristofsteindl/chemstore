import React, { Component } from 'react'
import ChemItem from './ChemItem'
import RedirectFormButton from '../RedirectFormButton'
import { check } from '../../utils/securityUtils'
import axios from 'axios'
import { connect } from 'react-redux'
import Pagination from '../Pagination'
import ChemItemContent from './ChemItemContent'

class ChemItemDashboard extends Component {

    constructor() {
        super()
        this.state = {
            chemItems: [],
            currentPage: 1, 
            totalItems: 0,
            totalPages: 0 
        }
    }

    componentDidMount() {
        const selectedLab = this.props.selectedLab
        this.loadChemItems(selectedLab)
    }

    async componentWillReceiveProps(nextProps){
        const selectedLab = nextProps.selectedLab
        this.loadChemItems(selectedLab)
    }
    
    loadChemItems(selectedLab) {
        check()
        if (selectedLab && selectedLab.key) {
            axios.get(`/api/chem-item/${selectedLab.key}?page=${0}&size=${3}`)
                .then(result => this.setState({
                    chemItems: result.data.content,
                    totalItems: result.data.totalItems,
                    totalPages: result.data.totalPages
                }))
                .catch(error => console.log(error))
        }
    }  
    
    onPageChanged = data => {
        check()
        const selectedLab = this.props.selectedLab
        const { currentPage, pageLimit } = data;
        if (selectedLab && selectedLab.key && currentPage) {
            
            console.log("on page changed " + currentPage)

            axios.get(`/api/chem-item/${selectedLab.key}?page=${currentPage - 1}&size=${pageLimit}`)
            .then(result => this.setState({
                chemItems: result.data.content,
                currentPage: currentPage,
                totalItems: result.data.totalItems,
                totalPages: result.data.totalPages
            }))
        }
      }

    render() {
        const { chemItems, totalItems, currentPage, totalPages } = this.state;

        return (
            <div className="projects">
                <div className="container">
                    <div className="row">
                        <div className="col-md-12">
                            <h3 className="display-4 text-center">Registered Chemicals</h3>
                            <RedirectFormButton formRoute="/add-chem-item" buttonLabel="Register Chemical"/>
                            <hr />
                            {this.props.selectedLab.key ? 
                                <ChemItemContent 
                                    chemItems={chemItems}
                                    totalItems={totalItems}
                                    currentPage={currentPage}
                                    totalPages={totalPages}
                                    onPageChanged={this.onPageChanged}

                                /> :
                                <p className="lead"><i>Please select a lab</i></p>
                            }

                        </div>
                    </div>
                </div>
            </div>
        )   
    }
}

const mapStateToProps = state => ({
    selectedLab: state.selectedLab
})

export default connect(mapStateToProps) (ChemItemDashboard)