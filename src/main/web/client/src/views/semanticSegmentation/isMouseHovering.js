import React, { PureComponent as Component } from 'react'

const isMouseOverElement = ({ innerRef, e }) => {
    var { pageY, pageX } = e;
    var { left, right, bottom, top } = innerRef.getBoundingClientRect();

    return pageX > left && pageX < right && pageY > top && pageY < bottom;
}

const isMouseHovering = (key = 'isMouseHovering') => DecoratedComponent => {
    class IsMouseHovering extends Component {
        constructor() {
            super()

            this.state = {
                isHoveringOver: false
            }
        }

        componentDidMount() {
            document.addEventListener('mousemove', this.onMouseMove)
        }

        componentWillUnmount() {
            document.removeEventListener('mousemove', this.onMouseMove)
        }

        onMouseMove = e => {
            const elem = this.el

            this.setState({
                isHoveringOver: isMouseOverElement({ elem, e })
            })
        }

        render() {
            const hocProps = {
                [key]: {
                    innerRef: el => this.el = el,
                    isHoveringOver: this.state.isHoveringOver
                }
            }

            return (
                <DecoratedComponent
                    {...this.props}
                    {...hocProps}
                />
            )
        }
    }

    IsMouseHovering.displayName = `IsMouseHovering(${DecoratedComponent.displayName})`

    return IsMouseHovering
}

export default isMouseHovering